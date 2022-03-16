package com.hanghae99.boilerplate.chat.model;

import com.hanghae99.boilerplate.chat.model.audiochat.AudioChatRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CloseChatRoomDto {
    private Long roomId;
    private AudioChatRole role; // MODERATOR 가 요청할 시에만 accept
    private String token;

    private Long totalParticipantCount;
    private Long agreeCount;
    private Long disagreeCount;

}
