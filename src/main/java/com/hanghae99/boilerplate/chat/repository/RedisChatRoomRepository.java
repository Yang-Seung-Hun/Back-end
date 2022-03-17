package com.hanghae99.boilerplate.chat.repository;


import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.model.dto.ChatRoomResDto;
import com.hanghae99.boilerplate.memberManager.model.Member;
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
        ChatRoom modRoom = chatRoom.addAgree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, modRoom);
        Long after = modRoom.getAgreeCount();
        return after;
    }

    public Long subAgree(String roomId) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoom modRoom = chatRoom.subAgree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, modRoom);
        Long after = modRoom.getAgreeCount();
        return after;
    }

    public Long addDisagree(String roomId) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoom modRoom = chatRoom.addDisagree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, modRoom);
        Long after = modRoom.getDisagreeCount();
        return after;
    }

    public Long subDisagree(String roomId) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoom modRoom = chatRoom.subDisagree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, modRoom);
        Long after = modRoom.getDisagreeCount();
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


    public ChatRoomResDto addParticipant(String roomId, Member member) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoom mChatRoom = chatRoom.addParticipant(member);
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mChatRoom);
        ChatRoomResDto dto = new ChatRoomResDto(mChatRoom);
        return dto;
    }

    public ChatRoomResDto subParticipant(String roomId, Member member) {
        ChatRoom chatRoom = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoom mChatRoom = chatRoom.subParticipant(member);
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mChatRoom);
        ChatRoomResDto dto = new ChatRoomResDto(mChatRoom);
        return dto;
    }
}
