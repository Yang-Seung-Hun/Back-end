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
    public ResponseEntity<ChatRoomRedisDto> createRoom(@RequestBody CreateChatRoomDto createChatRoomDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomRedisDto chatRoomRedisDto = chatRoomServiceImpl.save(createChatRoomDto, user);
        return ResponseEntity.ok().body(chatRoomRedisDto);
    }

    // 채팅방 입장
    @PostMapping("/auth/api/chat/room/join")
    public ResponseEntity<ChatRoomRedisDto> joinRoom(@RequestBody ChatEntryDto entryDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomRedisDto chatRoomRedisDto = chatRoomServiceImpl.addParticipant(entryDto, user);
        return ResponseEntity.ok().body(chatRoomRedisDto);
    }

    // 채팅방 떠남
    @PostMapping("/auth/api/chat/room/leave")
    public ResponseEntity<ChatRoomRedisDto> leaveRoom(@RequestBody ChatLeaveDto leaveDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomRedisDto chatRoomRedisDto = chatRoomServiceImpl.leaveParticipant(leaveDto, user);
        return ResponseEntity.ok().body(chatRoomRedisDto);
    }

    // 채팅방 종료
    @PostMapping("/auth/api/chat/room/close")
    public ResponseEntity<ChatRoomRedisDto> closeRoom(@RequestBody ChatCloseDto closeDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomRedisDto chatRoomRedisDto = chatRoomServiceImpl.closeRoom(closeDto, user);
        return ResponseEntity.ok().body(chatRoomRedisDto);
    }

    // 모든 채팅방 목록 조회
    @GetMapping("/api/chat/rooms")
    public ResponseEntity<List<ChatRoomResDto>> findAll() {
        List<ChatRoomResDto> allFromDb = chatRoomServiceImpl.findAllFromDb();
        return ResponseEntity.ok().body(allFromDb);
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
    public ResponseEntity<ChatRoomRedisDto> roomInfo(@PathVariable Long roomId) {
        ChatRoomRedisDto roomRedisDto = chatRoomServiceImpl.findByIdFromDb(roomId);
        return ResponseEntity.ok().body(roomRedisDto);
    }

    // 임시 - 전체삭제
    @DeleteMapping("/rooms/del/all")
    public String deleteAll() {
        chatRoomServiceImpl.deleteAll();
        return "모두 삭제완료!";
    }

}