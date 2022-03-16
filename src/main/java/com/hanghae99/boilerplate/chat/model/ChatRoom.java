package com.hanghae99.boilerplate.chat.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends Timestamped implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    private String roomName;   //방 제목
    private String moderator;  //방장(개설자)
    //todo participantCount: 수용인원 / 실참여인원 구분해서 네이밍해야 하겠지?
    private Long maxParticipantCount;
    private String content;  //토론 내용 (개요랄까)
    private Boolean isPrivate;  //비공개 여부 true: 비공개, false: 공개

    // todo 논의 : 채팅룸에 - 총 참여인원 어떻게 기록할지. 채팅 종료시점의 인원 or 최대 참여인원? 후자가 맞을 것 같다.
    private Long totalParticipantCount = 0L;

    //todo 찬반투표 수 - 채팅 종료시점의 기록만 가져오면 될 것 같다.
    private Long agreeCount = 0L;
    private Long disagreeCount= 0L;

    private LocalDateTime closedAt;
    private Boolean onAir = true;

    public void closeChatRoom(Long totalParticipantCount, Long agreeCount, Long disagreeCount, LocalDateTime closedAt, Boolean onAir) {
        this.totalParticipantCount = totalParticipantCount;
        this.agreeCount = agreeCount;
        this.disagreeCount = disagreeCount;
        this.closedAt = closedAt;
        this.onAir = onAir;
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
}