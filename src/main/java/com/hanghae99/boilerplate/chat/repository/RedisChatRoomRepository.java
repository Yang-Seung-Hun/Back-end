package com.hanghae99.boilerplate.chat.repository;


import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.model.dto.ChatLeaveDto;
import com.hanghae99.boilerplate.chat.model.dto.ChatRoomEntryResDto;
import com.hanghae99.boilerplate.chat.model.dto.ChatRoomRedisDto;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.trace.logtrace.LogTrace;
import com.hanghae99.boilerplate.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
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
    private final LogTrace trace;

    //db의존
    private final ChatRoomRepository chatRoomRepository;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    //채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash 에 저장
    public ChatRoomRedisDto createChatRoom( String roomId, ChatRoom chatRoom) {
        // template method pattern 적용 (익명 내부 클래스)
        AbstractTemplate<ChatRoomRedisDto> template = new AbstractTemplate<>(trace) {
            @Override
            protected ChatRoomRedisDto call() {
                ChatRoomRedisDto redisDto = new ChatRoomRedisDto(chatRoom);
                opsHashChatRoom.put(CHAT_ROOMS, roomId, redisDto);
                return redisDto;
            }
        };
        return template.execute("RedisChatRoomRepository.createChatRoom()");
    }

    //채팅방 입장
    public ChatRoomEntryResDto addParticipant(String roomId, Member member) {
        Optional<ChatRoomRedisDto> opitonalChatRoomRedisDto = Optional.ofNullable(opsHashChatRoom.get(CHAT_ROOMS, roomId));
        if (opitonalChatRoomRedisDto.isPresent()) {
            ChatRoomRedisDto chatRoomRedisDto = opitonalChatRoomRedisDto.get();
            ChatRoomRedisDto mChatRoomRedisDto = chatRoomRedisDto.addParticipant(member);
            opsHashChatRoom.put(CHAT_ROOMS, roomId, mChatRoomRedisDto);

            ChatRoomEntryResDto entryResDto = new ChatRoomEntryResDto(mChatRoomRedisDto);
            Boolean memberAgreed = (mChatRoomRedisDto.getAgreed().get(member.getId()) != null) ? mChatRoomRedisDto.getAgreed().get(member.getId()) : false;
            Boolean memberDisagreed = (mChatRoomRedisDto.getDisagreed().get(member.getId()) != null) ? mChatRoomRedisDto.getDisagreed().get(member.getId()) : false;

            entryResDto.setMemberAgreed(memberAgreed);
            entryResDto.setMemberDisagreed(memberDisagreed);

            return entryResDto;
        } else {
            Optional<ChatRoom> roomFromDb = chatRoomRepository.findById(Long.valueOf(roomId));
            if (roomFromDb.isPresent()) {
                if (roomFromDb.get().getOnAir() == true) {
                    ChatRoomRedisDto chatRoomRedisDto = new ChatRoomRedisDto(roomFromDb.get());
                    ChatRoomRedisDto mChatRoomRedisDto = chatRoomRedisDto.addParticipant(member);

                    opsHashChatRoom.put(CHAT_ROOMS, roomId, chatRoomRedisDto);

                    ChatRoomEntryResDto entryResDto = new ChatRoomEntryResDto(mChatRoomRedisDto);
                    Boolean memberAgreed = (mChatRoomRedisDto.getAgreed().get(member.getId()) != null) ? mChatRoomRedisDto.getAgreed().get(member.getId()) : false;
                    Boolean memberDisagreed = (mChatRoomRedisDto.getDisagreed().get(member.getId()) != null) ? mChatRoomRedisDto.getDisagreed().get(member.getId()) : false;

                    entryResDto.setMemberAgreed(memberAgreed);
                    entryResDto.setMemberDisagreed(memberDisagreed);

                    return entryResDto;

                } else {
                    throw new IllegalArgumentException("해당 Id의 chatRoom이 종료되었습니다.");
                }
            } else {
                throw new IllegalArgumentException("해당 Id의 chatRoom이 개설되지 않았습니다.");
            }
        }
    }

    //채팅방 퇴장
    //현참여인원
    public ChatRoomRedisDto subParticipant(String roomId, Member member, ChatLeaveDto leaveDto) {
        ChatRoomRedisDto chatRoomRedisDto = opsHashChatRoom.get(CHAT_ROOMS, roomId);
        ChatRoomRedisDto mChatRoomRedisDto = chatRoomRedisDto.subParticipant(member);
        ChatRoomRedisDto nChatRoomRedisDto = mChatRoomRedisDto.recordMemberAgreedOrDisagreed(member, leaveDto);
        opsHashChatRoom.put(CHAT_ROOMS, roomId, nChatRoomRedisDto);
        return nChatRoomRedisDto;
    }

    //채팅방 제거
    public void removeRoom(String roomId) {
        Long delete = opsHashChatRoom.delete(CHAT_ROOMS, roomId);
    }

    // ***************************** 실시간 찬반투표 *******************************

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

    // ***************************** 채팅방 종료시 최종 기록 업데이트 *******************************

    public Long reportAgreeCount(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getAgreeCount();
    }

    public Long reportDisagreeCount(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getDisagreeCount();
    }

    public Set<Long> reportTotalMaxParticipantsIds(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId).getTotalMaxParticipantsIds();
    }

    ///// (+ 보조)
    public ChatRoomRedisDto findChatRoomRedisDtoById(String roomId) {
        return opsHashChatRoom.get(CHAT_ROOMS, roomId);
    }

// ***************************** 조회 (라이브) *******************************

    // 전체 조회
    public List<ChatRoomRedisDto> findAllRoom() {
        // template method pattern 적용
        AbstractTemplate<List<ChatRoomRedisDto>> template = new AbstractTemplate<>(trace) {
            @Override
            protected List<ChatRoomRedisDto> call() {
                List<ChatRoomRedisDto> redisDtos = opsHashChatRoom.values(CHAT_ROOMS);
                return redisDtos;
            }
        };
        return template.execute("RedisChatRoomRepository.findOnair()");
    }

    // 카테고리로 조회
    public List<ChatRoomRedisDto> findByCategory(String category) {
        List<ChatRoomRedisDto> resultDtos = new ArrayList<>();

        List<ChatRoomRedisDto> all = opsHashChatRoom.values(CHAT_ROOMS);
        for (ChatRoomRedisDto redisDto : all) {
            if (redisDto.getCategory().equals(category)) {
                resultDtos.add(redisDto);
            }
        }
        return resultDtos;
    }

    // 키워드 조회
    public List<ChatRoomRedisDto> findByKeyword(String keyword) {
        List<ChatRoomRedisDto> resultDtos = new ArrayList<>();

        List<ChatRoomRedisDto> all = opsHashChatRoom.values(CHAT_ROOMS);
        for (ChatRoomRedisDto redisDto : all) {
            if ((redisDto.getRoomName() != null) && (redisDto.getRoomName().contains(keyword))) {
                resultDtos.add(redisDto);
            }
        }
        return resultDtos;
    }

}
