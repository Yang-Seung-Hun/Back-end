package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.model.ChatEntry;
import com.hanghae99.boilerplate.chat.model.ChatRole;
import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.model.dto.*;
import com.hanghae99.boilerplate.chat.repository.ChatEntryRepository;
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

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final MemberRepository memberRepository;
    private final ChatEntryRepository chatEntryRepository;

//    @Override
    @Transactional
    @CacheEvict(cacheNames = "CHATROOM_DTOS", allEntries = true)
    public ChatRoomRedisDto save(CreateChatRoomDto createChatRoomDto, MemberContext user) {

        Optional<Member> optionalMember = memberRepository.findById(user.getMemberId());
        validateMember(optionalMember);
        Member member = optionalMember.get();
        ChatRoom room = new ChatRoom(createChatRoomDto, member);
        //db
        chatRoomRepository.save(room);
        //redis
        ChatRoomRedisDto chatRoomRedisDto = redisChatRoomRepository.createChatRoom(room.getRoomId().toString(), room);
        return chatRoomRedisDto;
    }

//    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "CHATROOM_DTOS")
    public List<ChatRoomRedisDto> findAllFromDb() {
        return chatRoomRepository.findAll().stream()
                .map(ChatRoomRedisDto::new)
                .collect(toList());
    }

//    @Override
    @Transactional(readOnly = true)
    public ChatRoomRedisDto findByIdFromDb(Long roomId) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(roomId);
        validateChatRoom(optionalChatRoom);
        ChatRoom findRoom = optionalChatRoom.get();
        return new ChatRoomRedisDto(findRoom);
    }

    private void validateChatRoom(Optional<ChatRoom> optionalChatRoom) {
        if (!optionalChatRoom.isPresent()) {
            throw new IllegalArgumentException("해당 Id의 방이 없습니다.");
        }
    }

    @Transactional
    public ChatRoomRedisDto closeRoom(ChatCloseDto chatCloseDto, @AuthenticationPrincipal MemberContext user) {

        if (!ChatRole.MODERATOR.equals(chatCloseDto.getRole())) {
            throw new IllegalArgumentException("방장만이 방을 삭제할 수 있습니다.");
        }

        // 방 존재하는지 확인
        Long roomId = chatCloseDto.getRoomId();
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(roomId);
        validateChatRoom(optionalChatRoom);
        ChatRoom room = optionalChatRoom.get();
        // 이미 종료되었는지 확인
        if (room.getOnAir() == false) {
            throw new IllegalArgumentException("이미 종료된 방입니다.");
        }

        Long agreeCount = redisChatRoomRepository.reportAgreeCount(roomId.toString());
        Long disagreeCount = redisChatRoomRepository.reportDisagreeCount(roomId.toString());
        List<ChatEntry> totalEntries = redisChatRoomRepository.reportTotalMaxParticipantsIds(roomId.toString()).stream()
                .map(memberId -> {
                    Optional<Member> member = memberRepository.findById(memberId);
                    if (!member.isPresent()) {
                        log.error("{}의 member가 존재하지 않아 최종 참여 인원으로 update하지 못했습니다.", memberId);
                    }
                    ChatEntry chatEntry = new ChatEntry(member.get(), room);
                    chatEntryRepository.save(chatEntry);
                    return chatEntry;
                })
                .collect(toList());

        log.info("[찬반 집계] 채팅방 {}이 종료됩니다. 찬성: {}, 반대: {}", roomId, agreeCount, disagreeCount);

        // 이방에 대해 업데이트 : 찬성수, 반대수, 종료시간 + 최대참여자수, 종료여부
        ChatRoom chatRoom = room.closeChatRoom(agreeCount, disagreeCount, LocalDateTime.now(), totalEntries);
        ChatRoom finalRoom = chatRoomRepository.save(chatRoom);
        ChatRoomRedisDto chatRoomRedisDtoById = redisChatRoomRepository.findChatRoomRedisDtoById(roomId.toString());
        ChatRoomRedisDto chatRoomRedisDto = chatRoomRedisDtoById.updateFinal(finalRoom);

        // redis 에서 삭제
        redisChatRoomRepository.removeRoom(roomId.toString());

        return chatRoomRedisDto;
    }


    // 조건
    public List<ChatRoomRedisDto> findOnAirChatRooms() {
        //redis 에는 현재 진행중인 친구만 있을테니.
        return redisChatRoomRepository.findAllRoom();
//        return chatRoomRepository.findOnAirChatRooms();
    }

//    public List<ChatRoomRedisDto> findByKeyword(String keyword) {
//        return chatRoomRepository.findByKeyword(keyword);
//    }


    // Todo 카테고리로 검색하기


    public void deleteAll() {
        chatRoomRepository.deleteAll();
    }

    public ChatRoomRedisDto addParticipant(ChatEntryDto entryDto, MemberContext user) {
        Optional<Member> findMember = memberRepository.findById(user.getMemberId());
        validateMember(findMember);
        log.info("입장하려는 사람: {}", findMember.get().getNickname());
        Member member = findMember.get();

        ChatRoomRedisDto chatRoomRedisDto = redisChatRoomRepository.addParticipant(entryDto.getRoomId().toString(), member);
        return chatRoomRedisDto;
    }

    public ChatRoomRedisDto leaveParticipant(ChatLeaveDto leaveDto, MemberContext user) {
        Optional<Member> findMember = memberRepository.findById(user.getMemberId());
        validateMember(findMember);
        log.info("퇴장하려는 사람의 nickname: {}, role: {}", findMember.get().getNickname(), leaveDto.getRole());
        ChatRoomRedisDto chatRoomRedisDto = redisChatRoomRepository.subParticipant(leaveDto.getRoomId().toString(), findMember.get());
        return chatRoomRedisDto;
    }

    private void validateMember(Optional<Member> findMember) {
        if (findMember == null) {
            throw new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다.");
        }
    }

    public List<ChatRoomRedisDto> findOnAirChatRoomsByCategory(String category) {
        List<ChatRoomRedisDto> chatRoomRedisDtos = redisChatRoomRepository.findByCategory(category);
        return chatRoomRedisDtos;
    }
}
