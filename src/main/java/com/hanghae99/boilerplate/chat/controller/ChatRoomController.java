package com.hanghae99.boilerplate.chat.controller;

import com.hanghae99.boilerplate.chat.model.ChatRoomResDto;
import com.hanghae99.boilerplate.chat.model.CreateChatRoomDto;
import com.hanghae99.boilerplate.chat.repository.ChatRoomRepository;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import com.hanghae99.boilerplate.chat.service.ChatRoomServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(originPatterns = "*")
@Slf4j
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final ChatRoomServiceImpl chatRoomServiceImpl;

    // 채팅방 생성
    @PostMapping("/room")
    public ResponseEntity<ChatRoomResDto> createRoom(@RequestBody CreateChatRoomDto createChatRoomDto) {
        //todo 방생성시 동일한 이름 중복되지 않게 해야 할 것. (chatRoomService 에서)
        ChatRoomResDto roomResDto = chatRoomServiceImpl.save(createChatRoomDto);
        return ResponseEntity.ok().body(roomResDto);
    }

    // 모든 채팅방 목록 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResDto>> findAll() {
        List<ChatRoomResDto> allFromDb = chatRoomServiceImpl.findAllFromDb();
//        List<ChatRoomResDto> allFromRedis = chatRoomServiceImpl.findAllFromRedis();
        return ResponseEntity.ok().body(allFromDb);
//        return ResponseEntity.ok().body(allFromRedis);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ChatRoomResDto> roomInfo(@PathVariable Long roomId) {

        ChatRoomResDto roomResDto = chatRoomServiceImpl.findByIdFromDb(roomId);
//        ChatRoomResDto roomResDtoFromRedis = chatRoomServiceImpl.findByIdFromRedis(roomId);

        return ResponseEntity.ok().body(roomResDto);
//        return ResponseEntity.ok().body(mapRedis);
    }

}