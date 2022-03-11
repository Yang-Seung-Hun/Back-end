package com.hanghae99.boilerplate.model.chat;

import com.hanghae99.boilerplate.model.Timestamped;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends Timestamped {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    private String roomName;
    private String moderator;
    private Long participantCount;
    private String content;
    private Boolean isPrivate;

    public ChatRoom(ChatRoomDto dto) {
        this.roomName = dto.getRoomName();
        this.moderator = dto.getModerator();
        this.participantCount = dto.getParticipantCount();
        this.content = dto.getContent();
        this.isPrivate = dto.getIsPrivate();
    }

}