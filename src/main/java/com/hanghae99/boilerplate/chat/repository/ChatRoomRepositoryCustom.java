package com.hanghae99.boilerplate.chat.repository;

import com.hanghae99.boilerplate.chat.model.ChatRoomResDto;

import java.util.List;

public interface ChatRoomRepositoryCustom {
    List<ChatRoomResDto> findOnAirChatRooms();
    List<ChatRoomResDto> findByRoomName(String keyword);
}
