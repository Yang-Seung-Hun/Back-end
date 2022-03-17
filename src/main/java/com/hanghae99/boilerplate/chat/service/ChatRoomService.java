package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.model.dto.ChatCloseDto;
import com.hanghae99.boilerplate.chat.model.dto.ChatRoomResDto;
import com.hanghae99.boilerplate.chat.model.dto.CreateChatRoomDto;
import com.hanghae99.boilerplate.security.model.MemberContext;

import java.util.List;

public interface ChatRoomService {

    ChatRoomResDto save(CreateChatRoomDto createChatRoomDto);

    List<ChatRoomResDto> findAllFromDb();

    List<ChatRoomResDto> findAllFromRedis();

    ChatRoomResDto findByIdFromDb(Long roomId);

    ChatRoomResDto findByIdFromRedis(Long roomId);

    ChatRoomResDto closeRoom(ChatCloseDto chatCloseDto, MemberContext member);

    List<ChatRoomResDto> findOnAirChatRooms();

    List<ChatRoomResDto> findByKeyword(String keyword);

    }
