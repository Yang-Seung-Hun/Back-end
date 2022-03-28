package com.hanghae99.boilerplate.chat.model.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CreateChatRoomDto {
    private String roomName;
    private String category;
    private String moderator;
    private Long maxParticipantCount;
    private String content;
    private Boolean isPrivate;

    public CreateChatRoomDto(String roomName, String category, String moderator, Long maxParticipantCount, String content, Boolean isPrivate) {
        this.roomName = roomName;
        this.category = category;
        this.moderator = moderator;
        this.maxParticipantCount = maxParticipantCount;
        this.content = content;
        this.isPrivate = isPrivate;
    }
}
