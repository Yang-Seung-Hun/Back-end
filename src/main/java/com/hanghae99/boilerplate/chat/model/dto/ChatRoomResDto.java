package com.hanghae99.boilerplate.chat.model.dto;

import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class ChatRoomResDto implements Serializable {

    private Long roomId;
    private String roomName;   //방 제목
    private String moderator;  //방장(개설자)
    private Long maxParticipantCount;
    private String content;  //토론 내용 (개요랄까)
    private Boolean isPrivate;  //비공개 여부 true: 비공개, false: 공개
    private Set<Member> participants;
    private Long agreeCount = 0L;
    private Long disagreeCount= 0L;
    private LocalDateTime createAt;
    private LocalDateTime closedAt;
    private Boolean onAir = true;

    public ChatRoomResDto(ChatRoom room) {
        roomId = room.getRoomId();
        roomName = room.getRoomName();
//        moderator = room.getModerator();
        maxParticipantCount = room.getMaxParticipantCount();
        content = room.getContent();
        isPrivate = room.getIsPrivate();
//        participants = room.getParticipants();
        agreeCount = room.getAgreeCount();
        disagreeCount = room.getDisagreeCount();
        createAt = room.getCreatedAt();
        closedAt = room.getClosedAt();
        onAir = room.getOnAir();
    }

    @QueryProjection
    public ChatRoomResDto(Long roomId, String roomName, String moderator, Long maxParticipantCount, String content, Boolean isPrivate, Set<Member> participants, Long agreeCount, Long disagreeCount, LocalDateTime createAt, LocalDateTime closedAt, Boolean onAir) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.moderator = moderator;
        this.maxParticipantCount = maxParticipantCount;
        this.content = content;
        this.isPrivate = isPrivate;
        this.participants = participants;
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
        this.createAt = createAt;
        this.closedAt = closedAt;
        this.onAir = onAir;
    }
}
