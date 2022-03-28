package com.hanghae99.boilerplate.chat.controller;

import com.hanghae99.boilerplate.chat.model.dto.*;
import com.hanghae99.boilerplate.chat.service.ChatRoomService;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(originPatterns = "*")
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
//    private final TraceTemplate traceTemplate;

    // 채팅방 생성
    @PostMapping("/auth/api/chat/room")
    public ResponseEntity<ChatRoomCreateResDto> createRoom(@RequestBody CreateChatRoomDto createChatRoomDto, @AuthenticationPrincipal MemberContext user) {
        return ResponseEntity.ok().body(chatRoomService.createChatRoom(createChatRoomDto, user));
    }

    // 채팅방 입장
    @PostMapping("/auth/api/chat/room/join")
    public ResponseEntity<ChatRoomEntryResDto> joinRoom(@RequestBody ChatEntryDto entryDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomEntryResDto chatRoomEntryResDto = chatRoomService.addParticipant(entryDto, user);
        return ResponseEntity.ok().body(chatRoomEntryResDto);
    }

    // 채팅방 떠남
    @PostMapping("/auth/api/chat/room/leave")
    public ResponseEntity<ChatRoomRedisDto> leaveRoom(@RequestBody ChatLeaveDto leaveDto, @AuthenticationPrincipal MemberContext user) {
        return ResponseEntity.ok().body(chatRoomService.leaveParticipant(leaveDto, user));
    }

    // 채팅방 종료
    @PostMapping("/auth/api/chat/room/close")
    public ResponseEntity<ChatRoomRedisDto> closeRoom(@RequestBody ChatCloseDto closeDto, @AuthenticationPrincipal MemberContext user) {
        return ResponseEntity.ok().body(chatRoomService.closeRoom(closeDto, user));
    }

    //    ================ 라이브 중인 것에 대한 조회 ==================
    // 진행 중인 채팅방 조회
    @GetMapping("/api/chat/rooms/onair")
    public ResponseEntity<List<ChatRoomRedisDto>> findOnair() {
        return ResponseEntity.ok().body(chatRoomService.findOnAirChatRooms());
    }

    // 카테고리별 조회
    @GetMapping("/api/chat/rooms/onair/category/{category}")
    public ResponseEntity<List<ChatRoomRedisDto>> findOnAirChatRoomsByCategory(@PathVariable String category) {
        return ResponseEntity.ok().body(chatRoomService.findOnAirChatRoomsByCategory(category));
    }

    // 키워드 조회
    @GetMapping("/api/chat/rooms/onair/keyword/{keyword}")
    public ResponseEntity<List<ChatRoomRedisDto>> findOnAirChatRoomsByKeyword(@PathVariable String keyword) {
        return ResponseEntity.ok().body(chatRoomService.findOnAirChatRoomsByKeyword(keyword));
    }

}