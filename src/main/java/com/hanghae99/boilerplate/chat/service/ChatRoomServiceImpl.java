package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.model.ChatEntry;
import com.hanghae99.boilerplate.chat.model.ChatRole;
import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.model.dto.*;
import com.hanghae99.boilerplate.chat.repository.ChatEntryRepository;
import com.hanghae99.boilerplate.chat.repository.ChatRoomRepository;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
import com.hanghae99.boilerplate.chat.util.DateTimeComparator;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
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
    private final DateTimeComparator comparator;
//    private final TraceTemplate traceTemplate;

    //    ************************* 채팅방 (생성, 입장, 퇴장, 종료)  **************************
    // 채팅방 생성 ( db 에 생성, ->  redis )
    @Override
    @Transactional
    public ChatRoomCreateResDto createChatRoom(CreateChatRoomDto createChatRoomDto, MemberContext user) {
        Optional<Member> optionalMember = memberRepository.findById(user.getMemberId());
        validateMember(optionalMember);
        Member member = optionalMember.get();

        // createChatRoomDto 검증
        validateCreateChatRoomDto(createChatRoomDto);

        ChatRoom room = new ChatRoom(createChatRoomDto, member);
        //db
        chatRoomRepository.save(room);
        //redis
        ChatRoomRedisDto chatRoomRedisDto = redisChatRoomRepository.createChatRoom(room.getRoomId().toString(), room);
        ChatRoomCreateResDto chatRoomCreateResDto = new ChatRoomCreateResDto(chatRoomRedisDto);
        chatRoomCreateResDto.setMemberName(createChatRoomDto.getModerator());
        chatRoomCreateResDto.setRole(ChatRole.MODERATOR);
        return chatRoomCreateResDto;
//        });
    }

    private void validateCreateChatRoomDto(CreateChatRoomDto createChatRoomDto) {
        if (createChatRoomDto.getRoomName() == null || createChatRoomDto.getRoomName().strip().equals("")) {
            throw new IllegalArgumentException("개설하려는 방의 정보를 입력해주세요.");
        }
        if (createChatRoomDto.getCategory() == null || createChatRoomDto.getCategory().strip().equals("")) {
            throw new IllegalArgumentException("개설하려는 방의 카테고리를 입력해주세요.");
        }
        if (createChatRoomDto.getModerator() == null || createChatRoomDto.getModerator().strip().equals("")) {
            throw new IllegalArgumentException("방장 정보를 입력해주세요.");
        }
        if (createChatRoomDto.getContent() == null || createChatRoomDto.getContent().strip().equals("")) {
            throw new IllegalArgumentException("개설하려는 방에 대한 소개글을 입력해주세요.");
        }
        if (createChatRoomDto.getMaxParticipantCount() == null) {
            throw new IllegalArgumentException("개설하려는 방의 최대참여인원을 입력해주세요");
        }
    }

    // 채팅방 입장 ( redis )
    @Override
    public ChatRoomEntryResDto addParticipant(ChatEntryDto entryDto, MemberContext user) {
        Optional<Member> findMember = memberRepository.findById(user.getMemberId());
        validateMember(findMember);
        Member member = findMember.get();
        // entryDto 검증

        validateEntryDto(entryDto);

        ChatRoomEntryResDto entryResDto = redisChatRoomRepository.addParticipant(entryDto.getRoomId().toString(), member);
        entryResDto.setMemberName(entryDto.getMemberName());
        entryResDto.setRole(entryDto.getRole());
        return entryResDto;
    }

    private void validateEntryDto(ChatEntryDto entryDto) {
        if (entryDto.getRoomId() == null) {
            throw new IllegalArgumentException("입장하려는 방의 roomId를 입력해주세요.");
        }
        if (entryDto.getMemberName() == null || entryDto.getMemberName().strip().equals("")) {
            throw new IllegalArgumentException("참여자의 닉네임을 입력해주세요.");
        }
        if (entryDto.getRole() == null) {
            throw new IllegalArgumentException("참여자의 역할을 입력해주세요.");
        }
        if (entryDto.getParticipantCount() == null) {
            throw new IllegalArgumentException("참여하려는 방의 최대참여인원을 입력해주세요");
        }
    }

    // 채팅방 퇴장 ( redis )
    @Override
    public ChatRoomRedisDto leaveParticipant(ChatLeaveDto leaveDto, MemberContext user) {
        Optional<Member> findMember = memberRepository.findById(user.getMemberId());
        validateMember(findMember);

        validateLeaveDto(leaveDto);

        log.info("퇴장하려는 사람의 nickname: {}, role: {}", findMember.get().getNickname(), leaveDto.getRole());
        return redisChatRoomRepository.subParticipant(leaveDto.getRoomId().toString(), findMember.get(), leaveDto);
    }

    private void validateLeaveDto(ChatLeaveDto leaveDto) {
        if (leaveDto.getRoomId() == null) {
            throw new IllegalArgumentException("퇴장하려는 방의 roomId를 입력해주세요.");
        }
        if (leaveDto.getMemberName() == null || leaveDto.getMemberName().strip().equals("")) {
            throw new IllegalArgumentException("퇴장하려는 회원의 닉네임을 입력해주세요.");
        }
        if (leaveDto.getAgreed() == null) {
            throw new IllegalArgumentException("최종 찬성 여부를 입력해주세요.");
        }
        if (leaveDto.getDisagreed() == null) {
            throw new IllegalArgumentException("최종 반대 여부를 입력해주세요.");
        }
    }

    // 채팅방 종료 ( redis , -> db update )
    @Override
    @Transactional
    public ChatRoomRedisDto closeRoom(ChatCloseDto chatCloseDto, @AuthenticationPrincipal MemberContext user) {

        Optional<Member> findMember = memberRepository.findById(user.getMemberId());
        validateMember(findMember);

        // closeDto null 체크
        validateCloseDto(chatCloseDto);

        // 방 존재하는지 확인
        Long roomId = chatCloseDto.getRoomId();
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(roomId);
        validateChatRoom(optionalChatRoom);
        Optional<ChatRoomRedisDto> optionalChatRoomRedisDto = Optional.ofNullable(redisChatRoomRepository.findChatRoomRedisDtoById(roomId.toString()));
        if (optionalChatRoomRedisDto == null) {
            throw new IllegalArgumentException("이미 종료된 방입니다.");
        }
        ChatRoom room = optionalChatRoom.get();

        // 이미 종료되었는지 확인
        if (room.getOnAir() == false) {
            throw new IllegalArgumentException("이미 종료된 방입니다.");
        }

        // 권한 확인
        if (!ChatRole.MODERATOR.equals(chatCloseDto.getRole()) ||
                !optionalChatRoomRedisDto.get().getModeratorNickname().equals(user.getNickname())
        ) {
            throw new IllegalArgumentException("방장만이 방을 종료할 수 있습니다.");
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

    private void validateCloseDto(ChatCloseDto chatCloseDto) {
        if (chatCloseDto.getRoomId() == null) {
            throw new IllegalArgumentException("종료하려는 방의 roomId를 입력해주세요.");
        }
        if (chatCloseDto.getMemberName() == null || chatCloseDto.getMemberName().strip().equals("")) {
            throw new IllegalArgumentException("퇴장하려는 회원의 닉네임을 입력해주세요.");
        }
        if (chatCloseDto.getRole() == null) {
            throw new IllegalArgumentException("방을 종료하려는 사용자의 역할을 입력해주세요.");
        }
    }

//    ************************* 라이브 채팅방 조회 (from redis) **************************

    // 라이브 채팅방 조회 : 전체  ( redis )
    @Override
    public List<ChatRoomRedisDto> findOnAirChatRooms() {
        List<ChatRoomRedisDto> allRoomsOnAir = redisChatRoomRepository.findAllRoom();
        Collections.sort(allRoomsOnAir, comparator);
        return allRoomsOnAir;
//        });
    }

    // 라이브 채팅방 조회 : 카테고리  ( redis )
    @Override
    public List<ChatRoomRedisDto> findOnAirChatRoomsByCategory(String category) {
        List<ChatRoomRedisDto> chatRoomRedisDtos = redisChatRoomRepository.findByCategory(category);
        Collections.sort(chatRoomRedisDtos, comparator);
        return chatRoomRedisDtos;
//        });
    }

    // 라이브 채팅방 조회 : 키워드  ( redis )
    @Override
    public List<ChatRoomRedisDto> findOnAirChatRoomsByKeyword(String keyword) {
        List<ChatRoomRedisDto> chatRoomRedisDtos = redisChatRoomRepository.findByKeyword(keyword);
        Collections.sort(chatRoomRedisDtos, comparator);
        return chatRoomRedisDtos;
//        });
    }


//    ************************* 검증용 보조 method  **************************

    private void validateMember(Optional<Member> findMember) {
        if (findMember == null) {
            throw new IllegalArgumentException("해당 ID의 회원이 존재하지 않습니다.");
        }
    }

    private void validateChatRoom(Optional<ChatRoom> optionalChatRoom) {
        if (!optionalChatRoom.isPresent()) {
            throw new IllegalArgumentException("해당 Id의 방이 없습니다.");
        }
    }
}
