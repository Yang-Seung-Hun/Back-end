package com.hanghae99.boilerplate.chat.model.dto;

import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Getter
public class ChatRoomRedisDto implements Serializable {

    private Long roomId;
    private String roomName;
    private String category;
    private Long moderatorId;
    private String moderatorNickname;
    private Long maxParticipantCount;
    private String content;
    private Boolean isPrivate;
    private Set<Long> participantsIds = new HashSet<>();
    private Set<String> participantsNicknames = new HashSet<>();
    private Set<Long> totalMaxParticipantsIds = new HashSet<>();
    private Long agreeCount = 0L;
    private Long disagreeCount= 0L;
    private Boolean onAir = true;

    // 생성 : 초기 생성된 chatRoom 정보로부터 dto 도 만들어주기
    public ChatRoomRedisDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.category = chatRoom.getCategory();
        this.moderatorId = chatRoom.getModerator().getId();
        this.moderatorNickname = chatRoom.getModerator().getNickname();
        this.content = chatRoom.getContent();
        this.isPrivate = chatRoom.getIsPrivate();
    }

    // 실시간 변동 반영
    public ChatRoomRedisDto addAgree() {
        this.agreeCount ++;
        return this;
    }

    public ChatRoomRedisDto addDisagree() {
        this.disagreeCount ++;
        return this;
    }

    public ChatRoomRedisDto subAgree() {
        this.agreeCount --;
        return this;
    }

    public ChatRoomRedisDto subDisagree() {
        this.disagreeCount --;
        return this;
    }

    public ChatRoomRedisDto addParticipant(Member member) {
        this.participantsIds.add(member.getId());
        this.participantsNicknames.add(member.getNickname());

        this.totalMaxParticipantsIds.add(member.getId()); // 순간최대참여인원 기록이 필요할테니.
        return this;
    }

    public ChatRoomRedisDto subParticipant(Member member) {
        this.participantsIds.remove(member.getId());
        this.participantsNicknames.remove(member.getNickname());
        return this;
    }
}
