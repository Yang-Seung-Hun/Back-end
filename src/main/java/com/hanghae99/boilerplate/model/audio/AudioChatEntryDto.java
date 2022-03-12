package com.hanghae99.boilerplate.model.audio;

import lombok.Data;

@Data
public class AudioChatEntryDto {
    private Long roomId;
    private String memberName;
    private AudioChatRole role;
    private Long participantCount;
}
