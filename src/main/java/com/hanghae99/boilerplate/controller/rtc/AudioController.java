package com.hanghae99.boilerplate.controller.rtc;

import com.hanghae99.boilerplate.model.AudioChatMember;
import com.hanghae99.boilerplate.model.AudioChatRole;
import io.openvidu.java.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/audio")
public class AudioController {

    // OpenVidu object as entrypoint of the SDK
    private OpenVidu openVidu;

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
    public AudioController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
    }

    // 토큰을 발급하는 api. 음성 채팅방에 입장하는 지점.
    // AudioChatMember 형식에 맞춘 requestBody 를 받아와서 발급해주는 것이 좋지 않을까.
    @PostMapping(value = "/join")
    public ResponseEntity<Object> getSessionIdAndToken(@RequestBody AudioChatMember chatMember) {
        //todo 로그인과 연결 후에는 로그인하지 않은 사용자일 경우 unauthorized 로 400 리턴하는 로직 추가

        Long roomId = chatMember.getRoomId(); // 참여요청한 멤버가 들어가려는 방 고유번호

        OpenViduRole role = OpenViduRole.SUBSCRIBER; // 기본은 SUBSCRIBER
        AudioChatRole reqRole = chatMember.getRole(); // 참여요청한 멤버 정보에 담긴 role 에 따라 openVidu role 변경
        if (reqRole == AudioChatRole.MODERATOR) {
            role = OpenViduRole.SUBSCRIBER;
        } else if (reqRole == AudioChatRole.PUBLISHER) {
            role = OpenViduRole.PUBLISHER;
        }

        // 예제를 보면, connectionProperties Builder()로 생성할 때,
        // 서버에서 데이터 optional 하게 보낼 수 있음. => .data(<여기에 serverData>)
        // 나도 일단 넣어볼까 ,, 나중에 데이터 내용 다시 정하고, 지금은 참여를 희망한 chatMember 의 memberName 을 넣어보기.
        String serverData = chatMember.getMemberName();

        // role 과 optional 한 serverData 와 함께 WEBRTC 타입으로 connectionProperties 를 생성
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
                .role(role).data(serverData).build();


        // 이미 생성된 음성채팅방에 대한 참여 요청일 경우
        if (this.mapSessions.get(roomId) != null) {
            log.info("이미 존재하는 room 에 대한 참여요청입니다. roomId = {}", roomId);
            try {

                // 방금 막 생성한 connectionProperties 를 기반으로 token 만들기
                String token = this.mapSessions.get(roomId).createConnection(connectionProperties).getToken();

                // token 을 키로 하고 role 을 값으로 갖는 map 객체를 현재 roomId(=sessionName)를 키로 갖는 더 상위 map 객체의 값으로 넣음.
                this.mapSessionNamesTokens.get(roomId).put(token, role);

                // 참여요청 성공한 roomId, 요청한 memberName, 그리고 프론트에서 openvidu session connect 에 사용할 token 을 response 로 보내기
                // (아마 프론트에서는 OpenVidu 객체에 대해 -> .initSession() -> .connect(a, b) 식으로 연결할 때 파라미터 a 로 token 을 넣어야 하는 것 같음)
                Map<String, String> map = new HashMap<>();
                map.put("roomId", roomId.toString());
                map.put("memberName", chatMember.getMemberName());
                map.put("token", token);
                map.put("etc", "참여요청 성공!");

                return ResponseEntity.ok().body(map);


            } catch (Exception e) {
                log.error("기존 방에 참여를 요청했으나 exception 발생. errorMessage = {}", e.getMessage());
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            // 새로 음성채팅방을 개설하는 경우
            try {
                log.info("새로운 room 개설 요청입니다. roomId = {}", roomId);

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
                Map<String, String> map = new HashMap<>();
                map.put("roomId", roomId.toString());
                map.put("memberName", chatMember.getMemberName());
                map.put("token", token);
                map.put("etc", "개설요청 성공!");
                return ResponseEntity.ok().body(map);

            } catch (Exception e) {
                log.error("새로운 방 개설을 요청했으나 exception 발생. errorMessage = {}", e.getMessage());
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }

    }

}