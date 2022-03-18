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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final MemberRepository memberRepository;

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
    public List<ChatRoomResDto> findAllFromDb() {
        return chatRoomRepository.findAll().stream()
                .map(ChatRoomResDto::new)
                .collect(Collectors.toList());
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
        Set<Member> totalMembers = redisChatRoomRepository.reportTotalMaxParticipantsIds(roomId.toString()).stream()
                .map(memberId -> {
                    Optional<Member> member = memberRepository.findById(memberId);
                    if (!member.isPresent()) {
                        log.error("{}의 member가 존재하지 않아 최종 참여 인원으로 update하지 못했습니다.", memberId);
                    }
                    return member.get();
                })
                .collect(Collectors.toSet());

        log.info("[찬반 집계] 채팅방 {}이 종료됩니다. 찬성: {}, 반대: {}", roomId, agreeCount, disagreeCount);

        // 이방에 대해 업데이트 : 찬성수, 반대수, 종료시간 + 최대참여자수, 종료여부
        ChatRoom chatRoom = room.closeChatRoom(agreeCount, disagreeCount, LocalDateTime.now(), totalMembers);
        chatRoomRepository.save(chatRoom);
        // redis 에서 삭제
        redisChatRoomRepository.removeRoom(roomId.toString());
        return new ChatRoomRedisDto(chatRoom);
    }


    // 조건
    public List<ChatRoomResDto> findOnAirChatRooms() {
        return chatRoomRepository.findOnAirChatRooms();
    }

    public List<ChatRoomResDto> findByKeyword(String keyword) {
        return chatRoomRepository.findByKeyword(keyword);
    }

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
}
