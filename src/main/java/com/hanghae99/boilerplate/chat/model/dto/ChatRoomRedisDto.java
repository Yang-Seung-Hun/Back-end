package com.hanghae99.boilerplate.chat.model.dto;

import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    private Map<String, String> participantsProfileImageUrls = new HashMap<>();
    private Set<Long> totalMaxParticipantsIds = new HashSet<>();
    private Long agreeCount = 0L;
    private Long disagreeCount= 0L;
    private Boolean onAir = true;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    //todo 보낼 때는 =>  ex agree: true, disagree: false

    private Map<Long, Boolean> agreed = new HashMap<>();
    private Map<Long, Boolean> disagreed = new HashMap<>();

    // 생성 : 초기 생성된 chatRoom 정보로부터 dto 도 만들어주기
    public ChatRoomRedisDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getRoomId();
        this.roomName = chatRoom.getRoomName();
        this.category = chatRoom.getCategory();
        this.moderatorId = chatRoom.getModerator().getId();
        this.moderatorNickname = chatRoom.getModerator().getNickname();
        this.maxParticipantCount = chatRoom.getMaxParticipantCount();
        this.content = chatRoom.getContent();
        this.isPrivate = chatRoom.getIsPrivate();
        this.createdAt = chatRoom.getCreatedAt();
    }

    // final report(?)
    public ChatRoomRedisDto updateFinal(ChatRoom chatRoom) {
        this.onAir = chatRoom.getOnAir();
        this.closedAt = chatRoom.getClosedAt();
        return this;
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
        this.participantsProfileImageUrls.put(member.getNickname(), member.getProfileImageUrl());

        this.totalMaxParticipantsIds.add(member.getId()); // 순간최대참여인원 기록이 필요할테니.
        return this;
    }

    public ChatRoomRedisDto subParticipant(Member member) {
        this.participantsIds.remove(member.getId());
        this.participantsNicknames.remove(member.getNickname());
        this.participantsProfileImageUrls.remove(member.getNickname(), member.getProfileImageUrl()); // todo 문의 ) 닉네임이 겹치면 안 되네 !

        return this;
    }

    public ChatRoomRedisDto recordMemberAgreedOrDisagreed(Member member, ChatLeaveDto leaveDto) {
        this.agreed.put(member.getId(), leaveDto.getAgreed());
        this.disagreed.put(member.getId(), leaveDto.getDisagreed());
        return this;
    }

}
