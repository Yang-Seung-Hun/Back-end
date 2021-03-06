package com.hanghae99.boilerplate.chat.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class ChatEntry {

    protected ChatEntry() {}

    @GeneratedValue(strategy = GenerationType.IDENTITY) @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private ChatRoom chatRoom;

    public ChatEntry(Member member, ChatRoom chatRoom) {
        this.member = member;
        this.chatRoom = chatRoom;
    }
}
