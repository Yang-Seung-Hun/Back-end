package com.hanghae99.boilerplate.chat.repository;


import com.hanghae99.boilerplate.chat.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RedisChatRoomRepository {
    // Redis
    private static final String CHAT_ROOMS = "ONAIR_CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    //채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash 에 저장
    public ChatRoom createChatRoom(String roomId, ChatRoom chatRoom) {
        opsHashChatRoom.put(CHAT_ROOMS, roomId, chatRoom);
        return chatRoom;
    }

    public Long addAgree(String roomId) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        Long before = chatRoom.getAgreeCount();
        log.info("[before] chatRoom.addAgree 실행 전: {}", before);

        ChatRoom modRoom = chatRoom.addAgree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, modRoom);

        Long after = modRoom.getAgreeCount();
        log.info("[after] chatRoom.addAgree 실행 후: {}", after);

        return after;
    }

    public Long subAgree(String roomId) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        Long before = chatRoom.getAgreeCount();
        log.info("[before] chatRoom.subAgree 실행 전: {}", before);

        ChatRoom modRoom = chatRoom.subAgree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, modRoom);

        Long after = modRoom.getAgreeCount();
        log.info("[after] chatRoom.subAgree 실행 후: {}", after);

        return after;
    }

    public Long addDisagree(String roomId) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        Long before = chatRoom.getDisagreeCount();
        log.info("[before] chatRoom.addDisagree 실행 전: {}", before);

        ChatRoom modRoom = chatRoom.addDisagree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, modRoom);

        Long after = modRoom.getDisagreeCount();
        log.info("[after] chatRoom.addDisagree 실행 후: {}", after);

        return after;
    }

    public Long subDisagree(String roomId) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        Long before = chatRoom.getDisagreeCount();
        log.info("[before] chatRoom.subDisagree 실행 전: {}", before);

        ChatRoom modRoom = chatRoom.subDisagree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, modRoom);

        Long after = modRoom.getDisagreeCount();
        log.info("[after] chatRoom.subDisagree 실행 후: {}", after);

        return after;
    }

    public Long reportAgreeCount(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getAgreeCount();
    }

    public Long reportDisagreeCount(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getDisagreeCount();
    }

    public void removeRoom(String roomId) {
        opsHashChatRoom.delete(CHAT_ROOMS, roomId);
    }


}
