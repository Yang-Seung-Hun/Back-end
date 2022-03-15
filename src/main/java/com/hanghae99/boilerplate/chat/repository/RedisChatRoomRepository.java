package com.hanghae99.boilerplate.chat.repository;


import com.hanghae99.boilerplate.chat.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class RedisChatRoomRepository {
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
//        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }
    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    /*
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장
     */
    public ChatRoom createChatRoom(String roomId, ChatRoom chatRoom) {
        opsHashChatRoom.put(CHAT_ROOMS, roomId, chatRoom);
        return chatRoom;
    }

}
