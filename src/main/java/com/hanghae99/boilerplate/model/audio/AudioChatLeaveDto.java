package com.hanghae99.boilerplate.model.audio;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AudioChatLeaveDto {
    Long roomId;
    String memberName;
    String role;
    String token;
}

