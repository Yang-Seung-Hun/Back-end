package com.hanghae99.boilerplate.chat.model.textchat;

import com.hanghae99.boilerplate.chat.model.Timestamped;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessage extends Timestamped {

    // 메시지 타입 : 입장, 채팅, 퇴장 .. 찬반 및 취소
    public enum MessageType {
        ENTER, CHAT, LEAVE, AGREE, CANCEL_AGREE, DISAGREE, CANCEL_DISAGREE
    }
    private MessageType type; // 메시지 타입
    private Long roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
    private Long agreeCount;
    private Long disagreeCount;
    private String sentAt;
    private String profileUrl;
}
