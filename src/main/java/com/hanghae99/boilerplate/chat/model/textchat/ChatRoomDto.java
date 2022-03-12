package com.hanghae99.boilerplate.chat.model.textchat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ChatRoomDto {
    private String roomName;
    private String moderator;
    private Long participantCount;
    private String content;
    private Boolean isPrivate;
}
