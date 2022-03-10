package com.hanghae99.boilerplate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatRoom {
    private String roomId;
    private String roomName;
    private String moderator;

    public static ChatRoom create(String roomName, String moderator) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.roomName = roomName;
        chatRoom.moderator = moderator;
        return chatRoom;
    }
}