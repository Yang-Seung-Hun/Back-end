package com.hanghae99.boilerplate.chat.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatRoomResDto implements Serializable {

    private Long roomId;
    private String roomName;   //방 제목
    private String moderator;  //방장(개설자)
    private Long maxParticipantCount;
    private String content;  //토론 내용 (개요랄까)
    private Boolean isPrivate;  //비공개 여부 true: 비공개, false: 공개
    private Long totalParticipantCount = 0L;
    private Long agreeCount = 0L;
    private Long disagreeCount= 0L;
    private LocalDateTime createAt;
    private LocalDateTime closedAt;

    public ChatRoomResDto(ChatRoom room) {
        roomId = room.getRoomId();
        roomName = room.getRoomName();
        moderator = room.getModerator();
        maxParticipantCount = room.getMaxParticipantCount();
        content = room.getContent();
        isPrivate = room.getIsPrivate();
        totalParticipantCount = room.getTotalParticipantCount();
        agreeCount = room.getAgreeCount();
        disagreeCount = room.getDisagreeCount();
        createAt = room.getCreatedAt();
        closedAt = room.getClosedAt();
    }
}
