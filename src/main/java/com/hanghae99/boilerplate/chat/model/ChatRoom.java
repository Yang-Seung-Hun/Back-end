package com.hanghae99.boilerplate.chat.model;

import com.hanghae99.boilerplate.chat.model.dto.CreateChatRoomDto;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends Timestamped implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    private String roomName;   //방 제목
    private String moderator;  //방장(개설자)
    private Long maxParticipantCount;
    private String content;  //토론 내용 (개요랄까)
    private Boolean isPrivate;  //비공개 여부 true: 비공개, false: 공개

    @OneToMany(fetch = LAZY)
    private Set<Member> participants = new HashSet<>();

    private Long agreeCount = 0L;
    private Long disagreeCount= 0L;

    private LocalDateTime closedAt;
    private Boolean onAir = true;

    public void closeChatRoom(Long agreeCount, Long disagreeCount, LocalDateTime closedAt) {
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
        this.closedAt = closedAt;
        this.onAir = false;
    }

    public ChatRoom(CreateChatRoomDto dto) {
        this.roomName = dto.getRoomName();
        this.moderator = dto.getModerator();
        this.maxParticipantCount = dto.getMaxParticipantCount();
        this.content = dto.getContent();
        this.isPrivate = dto.getIsPrivate();
    }

    public ChatRoom addAgree() {
        this.agreeCount ++;
        return this;
    }

    public ChatRoom addDisagree() {
        this.disagreeCount ++;
        return this;
    }

    public ChatRoom subAgree() {
        this.agreeCount --;
        return this;
    }

    public ChatRoom subDisagree() {
        this.disagreeCount --;
        return this;
    }

    public ChatRoom addParticipant(Member member) {
        this.participants.add(member);
        return this;
    }

    public ChatRoom subParticipant(Member member) {
        this.participants.remove(member);
        return this;
    }
}