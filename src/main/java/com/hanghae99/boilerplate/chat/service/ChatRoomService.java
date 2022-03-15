package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.model.ChatRoomResDto;
import com.hanghae99.boilerplate.chat.model.CloseChatRoomDto;
import com.hanghae99.boilerplate.chat.model.CreateChatRoomDto;
import com.hanghae99.boilerplate.chat.repository.ChatRoomRepository;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;

    @Transactional
    public ChatRoomResDto save(CreateChatRoomDto createChatRoomDto) {
        ChatRoom room = new ChatRoom(createChatRoomDto);
        //db
        chatRoomRepository.save(room);
        ChatRoomResDto chatRoomResDto = new ChatRoomResDto(room);
        //redis
        redisChatRoomRepository.createChatRoom(room.getRoomId().toString(), room);

        return chatRoomResDto;
    }

    /*
    전체 조회
    (1) from db
    (2) from redis
    */
    @Transactional(readOnly = true)
    public List<ChatRoom> findAllFromDb() {
        return chatRoomRepository.findAll();
    }

    public List<ChatRoom> findAllFromRedis() {
        return redisChatRoomRepository.findAllRoom();
    }

    /*
    roomId로 조회
    (1) from db
    (2) from redis
    */
    @Transactional(readOnly = true)
    public ChatRoomResDto findByIdFromDb(Long roomId) {
        Optional<ChatRoom> room = getChatRoom(roomId);
        ChatRoom findRoom = room.get();
        return new ChatRoomResDto(findRoom);
    }

    public ChatRoomResDto findByIdFromRedis(Long roomId) {
        ChatRoom roomRedis = redisChatRoomRepository.findRoomById(roomId.toString());
        return new ChatRoomResDto(roomRedis);
    }

    @Transactional
    public ChatRoomResDto closeRoom(CloseChatRoomDto closeChatRoomDto) {

        Long roomId = closeChatRoomDto.getRoomId();
        Optional<ChatRoom> optionalChatRoom = getChatRoom(roomId);
        ChatRoom room = optionalChatRoom.get();

        // 최종 참여인원, 찬성수, 반대수를 업데이트
        room.setTotalParticipantCount(closeChatRoomDto.getTotalParticipantCount());
        room.setAgreeCount(closeChatRoomDto.getAgreeCount());
        room.setDisagreeCount(closeChatRoomDto.getDisagreeCount());
        // 종료시간
        LocalDateTime dateAndtime = LocalDateTime.now();
        room.setClosedAt(dateAndtime);

        //redis 에서도 제거


        return new ChatRoomResDto(room);
    }

    private Optional<ChatRoom> getChatRoom(Long roomId) {
        Optional<ChatRoom> room = chatRoomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new IllegalArgumentException("해당 아이디의 방이 존재하지 않습니다.");
        }
        return room;
    }


}
