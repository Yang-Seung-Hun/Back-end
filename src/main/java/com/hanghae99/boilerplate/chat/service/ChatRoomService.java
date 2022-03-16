package com.hanghae99.boilerplate.chat.service;

import com.hanghae99.boilerplate.chat.model.ChatRoomResDto;
import com.hanghae99.boilerplate.chat.model.CloseChatRoomDto;
import com.hanghae99.boilerplate.chat.model.CreateChatRoomDto;

import java.util.List;

public interface ChatRoomService {

    ChatRoomResDto save(CreateChatRoomDto createChatRoomDto);

    List<ChatRoomResDto> findAllFromDb();

    List<ChatRoomResDto> findAllFromRedis();

    ChatRoomResDto findByIdFromDb(Long roomId);

    ChatRoomResDto findByIdFromRedis(Long roomId);

    ChatRoomResDto closeRoom(CloseChatRoomDto closeChatRoomDto);

    List<ChatRoomResDto> findOnAirChatRooms();

    List<ChatRoomResDto> findByKeyword(String keyword);

    }
