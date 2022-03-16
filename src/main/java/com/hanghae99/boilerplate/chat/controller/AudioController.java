package com.hanghae99.boilerplate.chat.controller;

import com.hanghae99.boilerplate.chat.model.ChatRoomResDto;
import com.hanghae99.boilerplate.chat.model.CloseChatRoomDto;
import com.hanghae99.boilerplate.chat.model.audiochat.AudioChatEntryDto;
import com.hanghae99.boilerplate.chat.model.audiochat.AudioChatLeaveDto;
import com.hanghae99.boilerplate.chat.model.audiochat.AudioChatRole;
import com.hanghae99.boilerplate.chat.repository.ChatRoomRepository;
import com.hanghae99.boilerplate.chat.service.ChatRoomServiceImpl;
import io.openvidu.java.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/audio")
@CrossOrigin("*")
public class AudioController {

    // OpenVidu object as entrypoint of the SDK
    private OpenVidu openVidu;

    //todo 개별서버 메모리에만 존재하는 map 이 아니라, 여러 서버가 공유하는 redis에 반영해야 해..!

    // Collection to pair session names and OpenVidu Session objects
    private Map<Long, Session> mapSessions = new ConcurrentHashMap<>();

    // Collection to pair session names and tokens (the inner Map pairs tokens and
    // role associated)
    private Map<Long, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

    // URL where our OpenVidu server is listening
    private String OPENVIDU_URL;
    // Secret shared with our OpenVidu server
    private String SECRET;

    // openVidu-server와 연결하기 위한 컨트롤러에는 secret 과, url 이 필요하다~
    public AudioController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl, ChatRoomRepository chatRoomRepository, ChatRoomServiceImpl chatRoomServiceImpl) {
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
        this.chatRoomRepository = chatRoomRepository;
        this.chatRoomServiceImpl = chatRoomServiceImpl;
    }

    private ChatRoomRepository chatRoomRepository;

    private ChatRoomServiceImpl chatRoomServiceImpl;

    // 토큰을 발급하는 api. 음성 채팅방에 입장하는 지점.
    // AudioChatMember 형식에 맞춘 requestBody 를 받아와서 발급해주는 것이 좋지 않을까.
    @PostMapping(value = "/join")
    public ResponseEntity<Object> getToken(@RequestBody AudioChatEntryDto chatEntryDto) {
        //todo 로그인 처리

        Long roomId = chatEntryDto.getRoomId(); // 참여요청한 멤버가 들어가려는 방 고유번호

        OpenViduRole role = OpenViduRole.SUBSCRIBER; // 기본은 SUBSCRIBER
        AudioChatRole reqRole = chatEntryDto.getRole(); // 참여요청한 멤버 정보에 담긴 role 에 따라 openVidu role 변경
        if (reqRole == AudioChatRole.MODERATOR) {
            role = OpenViduRole.MODERATOR;
        } else if (reqRole == AudioChatRole.PUBLISHER) {
            role = OpenViduRole.PUBLISHER;
        }

        // 예제를 보면, connectionProperties Builder()로 생성할 때,
        // 서버에서 데이터 optional 하게 보낼 수 있음. => .data(<여기에 serverData>)
        // 나도 일단 넣어볼까 ,, 나중에 데이터 내용 다시 정하고, 지금은 참여를 희망한 chatMember 의 memberName 을 넣어보기.
        String serverData = chatEntryDto.getMemberName();

        // role 과 optional 한 serverData 와 함께 WEBRTC 타입으로 connectionProperties 를 생성
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
                .role(role).data(serverData).build();

        // 이미 생성된 음성채팅방에 대한 참여 요청일 경우
        if (this.mapSessions.get(roomId) != null) {
            log.info("이미 존재하는 room 에 대한 참여요청입니다. roomId = {}", roomId);
            try {

                //특정 roomId에 유효하게 발급되어 있는 token의 수 세기 위한 시도들
                log.info("{}번 room에 대해 발급된 유효한 token 개수는 {}", roomId, this.mapSessionNamesTokens.get(roomId).size());

                int nowParticipants = this.mapSessionNamesTokens.get(roomId).size();
                Long maxParticipants = chatEntryDto.getParticipantCount();
                if (maxParticipants <= nowParticipants) {
                    log.info("{}번 room에 대해 수용가능 인원이 이미 찼어요. 현재 인원:{}, 최대 인원:{}", roomId, nowParticipants, maxParticipants);
                    return ResponseEntity.badRequest().body("수용가능 인원이 이미 찼어요.");
                }

                // 방금 막 생성한 connectionProperties 를 기반으로 token 만들기
                String token = this.mapSessions.get(roomId).createConnection(connectionProperties).getToken();

                // token 을 키로 하고 role 을 값으로 갖는 map 객체를 현재 roomId(=sessionName)를 키로 갖는 더 상위 map 객체의 값으로 넣음.
                this.mapSessionNamesTokens.get(roomId).put(token, role);


                // 참여요청 성공한 roomId, 요청한 memberName, 그리고 프론트에서 openvidu session connect 에 사용할 token 을 response 로 보내기
                // (아마 프론트에서는 OpenVidu 객체에 대해 -> .initSession() -> .connect(a, b) 식으로 연결할 때 파라미터 a 로 token 을 넣어야 하는 것 같음)
                Map<String, String> map = getStringStringMap(chatEntryDto, roomId, role, token, "참여요청 성공");
                return ResponseEntity.ok().body(map);

            } catch (Exception e) {
                log.error("기존 방에 참여를 요청했으나 exception 발생. errorMessage = {}", e.getMessage());
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            // 새로 음성채팅방을 개설하는 경우
            try {
                log.info("새로운 room 개설 요청입니다. roomId = {}", roomId);

                if (chatEntryDto.getRole() != AudioChatRole.MODERATOR) {
                    log.error("방장만이 방 개설 요청을 할 수 있습니다. 현재 role: {}", chatEntryDto.getRole());
                    String message = "방장만이 방 개설 요청을 할 수 있습니다. 현재 role: "+ chatEntryDto.getRole();
                    throw new IllegalArgumentException(message);
                }

                // 새 openVidu session 을 만들기
                Session session = this.openVidu.createSession();
                // 방금 막 생성한 connectionProperties 를 기반으로 token 만들기
                String token = session.createConnection(connectionProperties).getToken();

                // 새로 개설된 방을 등록
                this.mapSessions.put(roomId, session);
                // 이 방에 대해 지금 참여자의 정보(token(key) 과 role(val))를 담기
                this.mapSessionNamesTokens.put(roomId, new ConcurrentHashMap<>());
                this.mapSessionNamesTokens.get(roomId).put(token, role);

                // 개설요청 성공한 roomId, 요청한 memberName, 그리고 프론트에서 openvidu session connect 에 사용할 token 을 response 로 보내기
                Map<String, String> map = getStringStringMap(chatEntryDto, roomId, role, token, "개설요청 성공");
                return ResponseEntity.ok().body(map);

            } catch (Exception e) {
                log.error("새로운 방 개설을 요청했으나 exception 발생. errorMessage = {}", e.getMessage());
                log.error("새로운 방 개설을 요청했으나 exception 발생. e = {}", e);
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    private Map<String, String> getStringStringMap(AudioChatEntryDto chatMember, Long roomId, OpenViduRole role, String token, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("roomId", roomId.toString());
        map.put("memberName", chatMember.getMemberName());
        map.put("token", token);
        map.put("role", role.toString());
        map.put("etc", message);
        return map;
    }

    @PostMapping(value = "/leave")
    public ResponseEntity<Object> leaveRoom(@RequestBody AudioChatLeaveDto chatLeaveDto) {

        log.info("채팅방 퇴장 요청입니다.", chatLeaveDto);

        Long roomId = chatLeaveDto.getRoomId();
        String token = chatLeaveDto.getToken();
        String memberName = chatLeaveDto.getMemberName();

        // 존재하는 방에 대한 퇴장 요청인 경우
        if (this.mapSessions.get(roomId) != null && this.mapSessionNamesTokens.get(roomId) != null) {

            // 유효한 토큰이었고, 삭제작업이 진행됨.
            if (this.mapSessionNamesTokens.get(roomId).remove(token) != null) {
                // 해당 멤버 퇴장 성공
                log.info("{}님이 room {}에서 퇴장 성공!", memberName, roomId);
                // 그런데 이 roomId에 대한 토큰이 전혀 없다면 ( 해당 음성채팅방 참여자가 남지 않았다면 )
                if (this.mapSessionNamesTokens.get(roomId).isEmpty()) {
                    // roomId 로 열린 session 삭제
                    this.mapSessions.remove(roomId);
                    log.info("더 이상 남아있는 사람이 없어요. 채팅방 {}도 삭제됩니다.", roomId);
                }
                // 또는 나가려는 사람이 방장이라면 ?
                if (chatLeaveDto.getRole().equals(OpenViduRole.MODERATOR.toString())) {
                    // roomId 로 열린 session 삭제
                    this.mapSessions.remove(roomId);
                    log.info("방장이 퇴장했으므로 채팅방 {}도 삭제됩니다.", roomId);
                }

                String message = roomId + "에 대한 퇴장 요청 성공, 퇴장한 memberName: " + memberName;
                return ResponseEntity.ok().body(message);
            } else {
                // 유효한 토큰이 아닌 경우
                log.info("유효하지 않은 토큰입니다! 제출한 토큰은 {}", token);
                String message = "유효하지 않은 토큰입니다! 제출한 토큰: " + token;
                throw new IllegalArgumentException(message);
            }
        } else {
            log.info("존재하지 않는 방에 대한 퇴장 요청입니다.", roomId);
            String message = "존재하지 않는 방에 대한 퇴장 요청입니다.";
            throw new IllegalArgumentException(message);
        }
    }

    @Transactional
    @PostMapping(value = "/close")
    public ResponseEntity<ChatRoomResDto> closeRoom(@RequestBody CloseChatRoomDto closeChatRoomDto) {

        Long roomId = closeChatRoomDto.getRoomId();
        String token = closeChatRoomDto.getToken();

        if (!AudioChatRole.MODERATOR.equals(closeChatRoomDto.getRole())) {
            throw new IllegalArgumentException("방장이 아니면 방을 종료할 수 없습니다.");
        }
        if (this.mapSessionNamesTokens.get(roomId).get(token) == null) {
            throw new IllegalArgumentException("유효한 토큰이 아니므로 방을 종료할 권한이 없습니다.");
        }

        ChatRoomResDto roomResDto = chatRoomServiceImpl.closeRoom(closeChatRoomDto);

        // 메모리 상에서만 관리되고 있는 거긴 하지만, 개설된 방 삭제. //todo 이 친구는 무용해질 것 .. 이걸 redis 에서 다루어야.
        this.mapSessionNamesTokens.remove(roomId);
        this.mapSessions.remove(roomId);

        return ResponseEntity.ok().body(roomResDto);
    }

}
