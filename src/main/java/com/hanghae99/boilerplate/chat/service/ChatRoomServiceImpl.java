package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.model.ChatRole;
import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.model.dto.*;
import com.hanghae99.boilerplate.chat.repository.ChatRoomRepository;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final MemberRepository memberRepository;

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

    @Transactional
    public ChatRoomResDto closeRoom(ChatCloseDto chatCloseDto, @AuthenticationPrincipal MemberContext user) {

        if (!ChatRole.MODERATOR.equals(chatCloseDto.getRole())) {
            throw new IllegalArgumentException("방장만이 방을 삭제할 수 있습니다.");
        }

        Long roomId = chatCloseDto.getRoomId();
        Optional<ChatRoom> optionalChatRoom = getChatRoom(roomId); //예외처리 포함
        ChatRoom room = optionalChatRoom.get();

        Long agreeCount = redisChatRoomRepository.reportAgreeCount(roomId.toString());
        Long disagreeCount = redisChatRoomRepository.reportDisagreeCount(roomId.toString());

        log.info("[찬반 집계] 채팅방 {}이 종료됩니다. 찬성: {}, 반대: {}", roomId, agreeCount, disagreeCount);

        // 이방에 대해 업데이트 : 찬성수, 반대수, 종료시간 + 최대참여자수, 종료여부
        room.closeChatRoom(agreeCount, disagreeCount, LocalDateTime.now());

        // redis 에서 삭제
        redisChatRoomRepository.removeRoom(roomId.toString());
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

    public ChatRoomResDto addParticipant(ChatEntryDto entryDto, MemberContext user) {
        Optional<Member> findMember = memberRepository.findById(user.getMemberId());
        validateMember(findMember);
        log.info("입장하려는 사람: {}", findMember.get().getNickname());
        ChatRoomResDto dto = redisChatRoomRepository.addParticipant(entryDto.getRoomId().toString(), findMember.get());
        return dto;
    }

    public ChatRoomResDto leaveParticipant(ChatLeaveDto leaveDto, MemberContext user) {
        Optional<Member> findMember = memberRepository.findById(user.getMemberId());
        validateMember(findMember);
        log.info("퇴장하려는 사람의 nickname: {}, role: {}", findMember.get().getNickname(), leaveDto.getRole());
        ChatRoomResDto dto = redisChatRoomRepository.subParticipant(leaveDto.getRoomId().toString(), findMember.get());
        return dto;
    }

    private void validateMember(Optional<Member> findMember) {
        if (findMember == null) {
            throw new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다.");
        }
    }
}
