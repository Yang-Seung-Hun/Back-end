package com.hanghae99.boilerplate.chat.repository;


import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.model.dto.ChatRoomRedisDto;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RedisChatRoomRepository {
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM_REDIS_DTOS";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoomRedisDto> opsHashChatRoom;

    //db의존
    private final ChatRoomRepository chatRoomRepository;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    public List<ChatRoomRedisDto> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    //채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash 에 저장
    public ChatRoomRedisDto createChatRoom(String roomId, ChatRoom chatRoom) {
        // chatRoom -> chatRoomRedisDto
        ChatRoomRedisDto redisDto = new ChatRoomRedisDto(chatRoom);
        opsHashChatRoom.put(CHAT_ROOMS, roomId, redisDto);
        return redisDto;
    }

    //채팅방 입장
    public ChatRoomRedisDto addParticipant(String roomId, Member member) {
        Optional<ChatRoomRedisDto> opitonalChatRoomRedisDto = Optional.ofNullable(opsHashChatRoom.get(CHAT_ROOMS, roomId));
        if (opitonalChatRoomRedisDto.isPresent()) {
            ChatRoomRedisDto chatRoomRedisDto = opitonalChatRoomRedisDto.get();
            ChatRoomRedisDto mChatRoomRedisDto = chatRoomRedisDto.addParticipant(member);
            opsHashChatRoom.put(CHAT_ROOMS, roomId, mChatRoomRedisDto);
            return mChatRoomRedisDto;
        } else {
            Optional<ChatRoom> roomFromDb = chatRoomRepository.findById(Long.valueOf(roomId));
            if (roomFromDb.isPresent()) {
                if (roomFromDb.get().getOnAir() == true) {
                    ChatRoomRedisDto chatRoomRedisDto = new ChatRoomRedisDto(roomFromDb.get());
                    ChatRoomRedisDto mChatRoomRedisDto = chatRoomRedisDto.addParticipant(member);
                    opsHashChatRoom.put(CHAT_ROOMS, roomId, chatRoomRedisDto);
                    return mChatRoomRedisDto;
                } else {
                    throw new IllegalArgumentException("해당 Id의 chatRoom이 종료되었습니다.");
                }
            } else {
                throw new IllegalArgumentException("해당 Id의 chatRoom이 개설되지 않았습니다.");
            }
        }
    }

    //채팅방 퇴장
    public ChatRoomRedisDto subParticipant(String roomId, Member member) {
        ChatRoomRedisDto chatRoomRedisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mChatRoomRedisDto = chatRoomRedisDto.subParticipant(member);

        opsHashChatRoom.put(CHAT_ROOMS, roomId, mChatRoomRedisDto);
        return mChatRoomRedisDto;
    }

    public void removeRoom(String roomId) {
        opsHashChatRoom.delete(CHAT_ROOMS, roomId);
    }

    public Long addAgree(String roomId) {
        ChatRoomRedisDto redisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mRedisDto = redisDto.addAgree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mRedisDto);
        Long after = mRedisDto.getAgreeCount();
        return after;
    }

    public Long subAgree(String roomId) {
        ChatRoomRedisDto redisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mRedisDto = redisDto.subAgree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mRedisDto);
        Long after = mRedisDto.getAgreeCount();
        return after;
    }

    public Long addDisagree(String roomId) {
        ChatRoomRedisDto redisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mRedisDto = redisDto.addDisagree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mRedisDto);
        Long after = mRedisDto.getDisagreeCount();
        return after;
    }

    public Long subDisagree(String roomId) {
        ChatRoomRedisDto redisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mRedisDto = redisDto.subDisagree();
        opsHashChatRoom.put(CHAT_ROOMS, roomId, mRedisDto);
        Long after = mRedisDto.getDisagreeCount();
        return after;
    }

    public Long reportAgreeCount(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getAgreeCount();
    }

    public Long reportDisagreeCount(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getDisagreeCount();
    }

    public Set<Long> reportTotalMaxParticipantsIds(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getTotalMaxParticipantsIds();
    }

    public ChatRoomRedisDto findChatRoomRedisDtoById(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId);
    }







}
