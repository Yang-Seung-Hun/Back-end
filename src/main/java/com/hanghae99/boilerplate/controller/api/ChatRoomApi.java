package com.hanghae99.boilerplate.controller.api;

import com.hanghae99.boilerplate.model.ChatRoom;
import com.hanghae99.boilerplate.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatRoomApi {

    private final ChatRoomRepository chatRoomRepository;

    // 모든 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> room() {
        List<ChatRoom> rooms = chatRoomRepository.findAllRoom();
        return ResponseEntity.ok().body(rooms);
    }

    // 채팅방 생성 (채팅방명을 파라미터로)
    @PostMapping("/room")
    public ResponseEntity<ChatRoom> createRoom(@RequestParam String name) {
        //todo 방생성시 동일한 이름 중복되지 않게 해야 할 것.
        ChatRoom room = chatRoomRepository.createChatRoom(name);
        return ResponseEntity.ok().body(room);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoom> roomInfo(@PathVariable String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        return ResponseEntity.ok().body(room);
    }
}