package com.hanghae99.boilerplate.chat.model.dto;

import com.hanghae99.boilerplate.chat.model.ChatRole;
import lombok.Data;

@Data
public class ChatEntryDto {
    private Long roomId;
    private String memberName;
    private ChatRole role;
    private Long participantCount;
}
