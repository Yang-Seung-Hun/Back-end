package com.hanghae99.boilerplate.chat.model.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Getter
public class ChatRoomEntryResDto {
    private Long roomId;
    private String roomName;
    private String category;
    private Long moderatorId;
    private String moderatorNickname;
    private Long maxParticipantCount;
    private String content;
    private Boolean isPrivate;
    private Set<String> participantsNicknames = new HashSet<>();
    private Map<String, String> participantsProfileImageUrls = new HashMap<>();
    private Long agreeCount = 0L;
    private Long disagreeCount= 0L;
    private Boolean onAir = true;
    private LocalDateTime createdAt;
    private Boolean memberAgreed;
    private Boolean memberDisagreed;

    public ChatRoomEntryResDto(ChatRoomRedisDto chatRoomRedisDto) {
        this.roomId = chatRoomRedisDto.getRoomId();
        this.roomName = chatRoomRedisDto.getRoomName();
        this.category = chatRoomRedisDto.getCategory();
        this.moderatorId = chatRoomRedisDto.getModeratorId();
        this.moderatorNickname = chatRoomRedisDto.getModeratorNickname();
        this.maxParticipantCount = chatRoomRedisDto.getMaxParticipantCount();
        this.content = chatRoomRedisDto.getContent();
        this.isPrivate = chatRoomRedisDto.getIsPrivate();
        this.participantsNicknames = chatRoomRedisDto.getParticipantsNicknames();
        this.participantsProfileImageUrls = chatRoomRedisDto.getParticipantsProfileImageUrls();
        this.agreeCount = chatRoomRedisDto.getAgreeCount();
        this.disagreeCount = chatRoomRedisDto.getDisagreeCount();
        this.onAir = chatRoomRedisDto.getOnAir();
        this.createdAt = chatRoomRedisDto.getCreatedAt();
    }
}


