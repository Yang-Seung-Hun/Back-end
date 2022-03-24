package com.hanghae99.boilerplate.chat.model;

import com.hanghae99.boilerplate.chat.model.dto.CreateChatRoomDto;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends Timestamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    private String roomName;
    private String category;

    @ManyToOne
    private Member moderator;
    private Long maxParticipantCount;
    private String content;
    private Boolean isPrivate;

    @BatchSize(size = 500)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ChatEntry> entries = new ArrayList<>();

    private Long agreeCount = 0L;
    private Long disagreeCount= 0L;
    private LocalDateTime closedAt; 
    private Boolean onAir = true;

    public ChatRoom(CreateChatRoomDto dto, Member member) {
        this.roomName = dto.getRoomName();
        this.category = dto.getCategory();
        this.maxParticipantCount = dto.getMaxParticipantCount();
        this.content = dto.getContent();
        this.isPrivate = dto.getIsPrivate();
        this.moderator = member;

    }

    public ChatRoom closeChatRoom(Long agreeCount, Long disagreeCount, LocalDateTime closedAt, List<ChatEntry> entries) {
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
        this.closedAt = closedAt;
        this.onAir = false;
        this.entries = entries;
        return this;
    }

}