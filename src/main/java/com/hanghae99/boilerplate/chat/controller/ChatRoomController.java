package com.hanghae99.boilerplate.chat.controller;

import com.hanghae99.boilerplate.chat.model.dto.*;
import com.hanghae99.boilerplate.chat.service.ChatRoomServiceImpl;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@RequiredArgsConstructor
@RestController
//@RequestMapping("/auth/api/chat")
@CrossOrigin(originPatterns = "*")
@Slf4j
public class ChatRoomController {

    private final ChatRoomServiceImpl chatRoomServiceImpl;

    // 채팅방 생성
    @PostMapping("/auth/api/chat/room")
    public ResponseEntity<ChatRoomRedisDto> createRoom(@RequestBody CreateChatRoomDto createChatRoomDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomRedisDto chatRoomRedisDto = chatRoomServiceImpl.createChatRoom(createChatRoomDto, user);
        return ResponseEntity.ok().body(chatRoomRedisDto);
    }

    // 채팅방 입장
    @PostMapping("/auth/api/chat/room/join")
    public ResponseEntity<ChatRoomEntryResDto> joinRoom(@RequestBody ChatEntryDto entryDto, @AuthenticationPrincipal MemberContext user) {
        ChatRoomEntryResDto chatRoomEntryResDto = chatRoomServiceImpl.addParticipant(entryDto, user);
        return ResponseEntity.ok().body(chatRoomEntryResDto);
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

//    ================ 라이브 중인 것에 대한 조회 ==================
    // 진행 중인 채팅방 조회
    @GetMapping("/api/chat/rooms/onair")
    public ResponseEntity<List<ChatRoomRedisDto>> findOnair() throws UnsupportedEncodingException {
//
//        // making a sample for decoding test
//        String encoded = URLEncoder.encode("개설", "UTF-8");
//        log.info("개설 -> encoded : {}", encoded);

        List<ChatRoomRedisDto> chatrooms =  chatRoomServiceImpl.findOnAirChatRooms();
        return ResponseEntity.ok().body(chatrooms);
    }

    // 카테고리별 조회
    @GetMapping("/api/chat/rooms/onair/category/{category}")
    public ResponseEntity<List<ChatRoomRedisDto>> findOnAirChatRoomsByCategory(@PathVariable String category) {
        List<ChatRoomRedisDto> chatRooms = chatRoomServiceImpl.findOnAirChatRoomsByCategory(category);
        return ResponseEntity.ok().body(chatRooms);
    }

    // 키워드 조회
    @GetMapping("/api/chat/rooms/onair/keyword/{keyword}")
    public ResponseEntity<List<ChatRoomRedisDto>> findOnAirChatRoomsByKeyword(@PathVariable String keyword) throws UnsupportedEncodingException {
        // spring boot 가 알아서 url decoding 해주나봐..!
//        String decodedKeyword = URLDecoder.decode(keyword, "UTF-8");
//        log.info("👀 keyword 조회에서 encodedKeyword: {}", keyword); // -> encoding 된 걸로 검색해도 "개설"이라고 로그가 뜸!
//        log.info("👍 keyword 조회에서 decodedKeyword: {}", decodedKeyword);
        List<ChatRoomRedisDto> chatRooms = chatRoomServiceImpl.findOnAirChatRoomsByKeyword(keyword);
        return ResponseEntity.ok().body(chatRooms);
    }

//    ======================== 와이어프레임상 필수가 아닌 것 같은 api.

//    // 모든 채팅방 목록 조회
//    @GetMapping("/api/chat/rooms")
//    public ResponseEntity<List<ChatRoomRedisDto>> findAll() {
//        List<ChatRoomRedisDto> allFromDb = chatRoomServiceImpl.findAllFromDb();
//        return ResponseEntity.ok().body(allFromDb);
//    }
//
//    // 특정 채팅방 조회
//    @GetMapping("/room/{roomId}")
//    public ResponseEntity<ChatRoomRedisDto> roomInfo(@PathVariable Long roomId) {
//        ChatRoomRedisDto roomRedisDto = chatRoomServiceImpl.findByIdFromDb(roomId);
//        return ResponseEntity.ok().body(roomRedisDto);
//    }
//
//    // 임시 - 전체삭제
//    @DeleteMapping("/api/chat/rooms/del/all")
//    public String deleteAll() {
//        chatRoomServiceImpl.deleteAll();
//        return "모두 삭제완료!";
//    }

}