package com.hanghae99.boilerplate.chat.model.dto;

import com.hanghae99.boilerplate.chat.model.ChatRole;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChatEntryDto {
    @NotNull
    private Long roomId;
    @NotNull
    private String memberName;
    @NotNull
    private ChatRole role;
    @NotNull
    private Long participantCount;
}
