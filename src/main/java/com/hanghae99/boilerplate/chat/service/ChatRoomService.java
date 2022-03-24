package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.model.dto.*;
import com.hanghae99.boilerplate.security.model.MemberContext;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

public interface ChatRoomService {

    ChatRoomCreateResDto createChatRoom(CreateChatRoomDto createChatRoomDto, MemberContext user);

    ChatRoomEntryResDto addParticipant(ChatEntryDto entryDto, MemberContext user);

    ChatRoomRedisDto leaveParticipant(ChatLeaveDto leaveDto, MemberContext user);

    ChatRoomRedisDto closeRoom(ChatCloseDto chatCloseDto, @AuthenticationPrincipal MemberContext user);

    List<ChatRoomRedisDto> findOnAirChatRooms();

    List<ChatRoomRedisDto> findOnAirChatRoomsByCategory(String category);

    List<ChatRoomRedisDto> findOnAirChatRoomsByKeyword(String keyword);

    }
