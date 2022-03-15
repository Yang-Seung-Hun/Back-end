package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.model.ChatRoomResDto;
import com.hanghae99.boilerplate.chat.model.CloseChatRoomDto;
import com.hanghae99.boilerplate.chat.model.CreateChatRoomDto;
import com.hanghae99.boilerplate.chat.repository.ChatRoomRepository;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;

    @Override
    @Transactional
    @CacheEvict(cacheNames = "CHATROOM_DTOS", allEntries = true) // , key = "#username")
    public ChatRoomResDto save(CreateChatRoomDto createChatRoomDto) {
        ChatRoom room = new ChatRoom(createChatRoomDto);
        //db
        chatRoomRepository.save(room);
        ChatRoomResDto chatRoomResDto = new ChatRoomResDto(room);
        //redis
        redisChatRoomRepository.createChatRoom(room.getRoomId().toString(), room);
        return chatRoomResDto;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "CHATROOM_DTOS")
    public List<ChatRoomResDto> findAllFromDb() {
        return chatRoomRepository.findAll().stream()
                .map(ChatRoomResDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatRoomResDto> findAllFromRedis() {
        return redisChatRoomRepository.findAllRoom().stream()
                .map(ChatRoomResDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatRoomResDto findByIdFromDb(Long roomId) {
        Optional<ChatRoom> room = getChatRoom(roomId);
        ChatRoom findRoom = room.get();
        return new ChatRoomResDto(findRoom);
    }

    @Override
    public ChatRoomResDto findByIdFromRedis(Long roomId) {
        ChatRoom roomRedis = redisChatRoomRepository.findRoomById(roomId.toString());
        return new ChatRoomResDto(roomRedis);
    }

    @Override
    @Transactional
    public ChatRoomResDto closeRoom(CloseChatRoomDto closeChatRoomDto) {

        Long roomId = closeChatRoomDto.getRoomId();
        Optional<ChatRoom> optionalChatRoom = getChatRoom(roomId); //예외처리 포함
        ChatRoom room = optionalChatRoom.get();

        Long totalParticipantCount = closeChatRoomDto.getTotalParticipantCount();
        Long agreeCount = closeChatRoomDto.getAgreeCount();
        Long disagreeCount = closeChatRoomDto.getDisagreeCount();

        // 최종 참여인원, 찬성수, 반대수, 종료시간, 진행중 여부를 업데이트
        room.closeChatRoom(totalParticipantCount, agreeCount, disagreeCount, LocalDateTime.now(), false);

        return new ChatRoomResDto(room);
    }

    // 예외처리
    private Optional<ChatRoom> getChatRoom(Long roomId) {
        Optional<ChatRoom> room = chatRoomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new IllegalArgumentException("해당 아이디의 방이 존재하지 않습니다.");
        }
        return room;
    }

    // 조건
    @Override
    public List<ChatRoomResDto> findOnAirChatRooms() {
        return chatRoomRepository.findOnAirChatRooms();
    }

    @Override
    public List<ChatRoomResDto> findByKeyword(String keyword) {
        return chatRoomRepository.findByKeyword(keyword);
    }


    public void deleteAll() {
        chatRoomRepository.deleteAll();
    }
}
