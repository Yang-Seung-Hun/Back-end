package com.hanghae99.boilerplate.chat.controller;

import com.hanghae99.boilerplate.chat.model.dto.*;
import com.hanghae99.boilerplate.chat.service.ChatRoomService;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.trace.logtrace.LogTrace;
import com.hanghae99.boilerplate.trace.template.AbstractTemplate;
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
    private final LogTrace trace;

    // 채팅방 생성
    @PostMapping("/auth/api/chat/room")
    public ResponseEntity<ChatRoomCreateResDto> createRoom(@RequestBody CreateChatRoomDto createChatRoomDto, @AuthenticationPrincipal MemberContext user) {
        // template method pattern 적용 (익명 내부 클래스)
        AbstractTemplate<ResponseEntity<ChatRoomCreateResDto>> template = new AbstractTemplate<>(trace) {
            @Override
            protected ResponseEntity<ChatRoomCreateResDto> call() {
                return ResponseEntity.ok().body(chatRoomService.createChatRoom(createChatRoomDto, user));
            }
        };
        return template.execute("ChatRoomController.createRoom()");
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
        ChatRoomRedisDto chatRoomRedisDto = chatRoomService.leaveParticipant(leaveDto, user);
        return ResponseEntity.ok().body(chatRoomRedisDto);
    }

    // 채팅방 종료
    @PostMapping("/auth/api/chat/room/close")
    public ResponseEntity<ChatRoomRedisDto> closeRoom(@RequestBody ChatCloseDto closeDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomRedisDto chatRoomRedisDto = chatRoomService.closeRoom(closeDto, user);
        return ResponseEntity.ok().body(chatRoomRedisDto);
    }

//    ================ 라이브 중인 것에 대한 조회 ==================
    // 진행 중인 채팅방 조회
    @GetMapping("/api/chat/rooms/onair")
    public ResponseEntity<List<ChatRoomRedisDto>> findOnair() {
        // template method pattern 적용
        AbstractTemplate<ResponseEntity<List<ChatRoomRedisDto>>> template = new AbstractTemplate<>(trace) {
            @Override
            protected ResponseEntity<List<ChatRoomRedisDto>> call() {
                return ResponseEntity.ok().body(chatRoomService.findOnAirChatRooms());
            }
        };
        return template.execute("ChatRoomController.findOnair()");
    }

    // 카테고리별 조회
    @GetMapping("/api/chat/rooms/onair/category/{category}")
    public ResponseEntity<List<ChatRoomRedisDto>> findOnAirChatRoomsByCategory(@PathVariable String category) {
        List<ChatRoomRedisDto> chatRooms = chatRoomService.findOnAirChatRoomsByCategory(category);
        return ResponseEntity.ok().body(chatRooms);
    }

    // 키워드 조회
    @GetMapping("/api/chat/rooms/onair/keyword/{keyword}")
    public ResponseEntity<List<ChatRoomRedisDto>> findOnAirChatRoomsByKeyword(@PathVariable String keyword) {
        List<ChatRoomRedisDto> chatRooms = chatRoomService.findOnAirChatRoomsByKeyword(keyword);
        return ResponseEntity.ok().body(chatRooms);
    }

}