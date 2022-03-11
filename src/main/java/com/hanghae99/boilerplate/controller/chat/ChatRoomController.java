package com.hanghae99.boilerplate.controller.chat;

import com.hanghae99.boilerplate.model.chat.ChatRoom;
import com.hanghae99.boilerplate.model.chat.ChatRoomDto;
import com.hanghae99.boilerplate.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(originPatterns = "*")
@Slf4j
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    // 모든 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> room() {
        List<ChatRoom> rooms = chatRoomRepository.findAll();
        return ResponseEntity.ok().body(rooms);
    }

    // 채팅방 생성 (채팅방명을 파라미터로)
    @PostMapping("/room")
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody ChatRoomDto chatRoomDto) {
        //todo 방생성시 동일한 이름 중복되지 않게 해야 할 것.
        ChatRoom room = new ChatRoom(chatRoomDto);
        chatRoomRepository.save(room);
        Map<String, Object> map = mapChatRoomInfo(room);
        return ResponseEntity.ok().body(map);
    }

    // 특정 채팅방 조회
    //todo chatRoomService 만들어서 entity를 controller 에서 다루지 않도록.
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Object> roomInfo(@PathVariable Long roomId) {
        Optional<ChatRoom> room = chatRoomRepository.findById(roomId);
        if (!room.isPresent()) {
            return ResponseEntity.badRequest().body("존재하지 않는 roomId 입니다.");
        }
        ChatRoom findRoom = room.get();
        Map<String, Object> map = mapChatRoomInfo(findRoom);
        return ResponseEntity.ok().body(map);
    }

    private Map<String, Object> mapChatRoomInfo(ChatRoom findRoom) {
        Map<String, Object> map = new HashMap<>();
        map.put("roomId", findRoom.getRoomId());
        map.put("roomName", findRoom.getRoomName());
        map.put("moderator", findRoom.getModerator());
        map.put("participantCount", findRoom.getParticipantCount());
        map.put("content", findRoom.getContent());
        map.put("isPrivate", findRoom.getIsPrivate());
        return map;
    }
}