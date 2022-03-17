package com.hanghae99.boilerplate.chat.controller;

import com.hanghae99.boilerplate.chat.model.dto.*;
import com.hanghae99.boilerplate.chat.repository.ChatRoomRepository;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import com.hanghae99.boilerplate.chat.service.ChatRoomServiceImpl;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
//@RequestMapping("/auth/api/chat")
@CrossOrigin(originPatterns = "*")
@Slf4j
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final ChatRoomServiceImpl chatRoomServiceImpl;

    // 채팅방 생성
    @PostMapping("/auth/api/chat/room")
    public ResponseEntity<ChatRoomResDto> createRoom(@RequestBody CreateChatRoomDto createChatRoomDto) {
        //todo 방생성시 동일한 이름 중복되지 않게 해야 할 것. (chatRoomService 에서)
        ChatRoomResDto roomResDto = chatRoomServiceImpl.save(createChatRoomDto);
        return ResponseEntity.ok().body(roomResDto);
    }

    // 채팅방 입장
    @PostMapping("/auth/api/chat/room/join")
    public ResponseEntity joinRoom(@RequestBody ChatEntryDto entryDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomResDto roomResDto = chatRoomServiceImpl.addParticipant(entryDto, user);
        return ResponseEntity.ok().body(roomResDto.getParticipants().size());
    }

    // 채팅방 떠남
    @PostMapping("/auth/api/chat/room/leave")
    public ResponseEntity leaveRoom(@RequestBody ChatLeaveDto leaveDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomResDto roomResDto = chatRoomServiceImpl.leaveParticipant(leaveDto, user);
        return ResponseEntity.ok().body(roomResDto.getParticipants().size());
    }

    // 채팅방 종료
    @PostMapping("/auth/api/chat/room/close")
    public ResponseEntity closeRoom(@RequestBody ChatCloseDto closeDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomResDto chatRoomResDto = chatRoomServiceImpl.closeRoom(closeDto, user);
        return ResponseEntity.ok().body("종료시간" + chatRoomResDto.getClosedAt());
    }


    // 모든 채팅방 목록 조회
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<List<ChatRoomResDto>> findAll() {
        List<ChatRoomResDto> allFromDb = chatRoomServiceImpl.findAllFromDb();
//        List<ChatRoomResDto> allFromRedis = chatRoomServiceImpl.findAllFromRedis();
        return ResponseEntity.ok().body(allFromDb);
//        return ResponseEntity.ok().body(allFromRedis);
    }

    // 진행 중인 채팅방 조회 : 어떤 채팅방이든 종료시 cache evict
    @GetMapping("/rooms/onair")
    public ResponseEntity<List<ChatRoomResDto>> findOnair() {
        List<ChatRoomResDto> chatrooms =  chatRoomServiceImpl.findOnAirChatRooms();
        return ResponseEntity.ok().body(chatrooms);
    }

    // 채팅방 name ( + contents) 중에서 키워드 검색
    @GetMapping("/rooms/{keyword}")
    public ResponseEntity<List<ChatRoomResDto>> findByKeyword(@PathVariable String keyword) {
        List<ChatRoomResDto> chatrooms =  chatRoomServiceImpl.findByKeyword(keyword);
        return ResponseEntity.ok().body(chatrooms);
    }


    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomResDto> roomInfo(@PathVariable Long roomId) {

        ChatRoomResDto roomResDto = chatRoomServiceImpl.findByIdFromDb(roomId);
//        ChatRoomResDto roomResDtoFromRedis = chatRoomServiceImpl.findByIdFromRedis(roomId);

        return ResponseEntity.ok().body(roomResDto);
//        return ResponseEntity.ok().body(mapRedis);
    }

    // 임시 - 전체삭제
    @DeleteMapping("/rooms/del/all")
    public String deleteAll() {
        chatRoomServiceImpl.deleteAll();
        return "모두 삭제완료!";
    }

}