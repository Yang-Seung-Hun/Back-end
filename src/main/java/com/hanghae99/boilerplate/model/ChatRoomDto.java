package com.hanghae99.boilerplate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ChatRoomDto {
    private String roomName;
    private String moderator;
}
