package com.hanghae99.boilerplate.chat.repository;

import com.hanghae99.boilerplate.chat.model.ChatRoomResDto;
import com.hanghae99.boilerplate.chat.model.QChatRoomResDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;

import static com.hanghae99.boilerplate.chat.model.QChatRoom.chatRoom;

public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ChatRoomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

    @Override
    public List<ChatRoomResDto> findOnAirChatRooms() {
        return queryFactory
                .select(new QChatRoomResDto(
                        chatRoom.roomId,
                        chatRoom.roomName,
                        chatRoom.moderator,
                        chatRoom.maxParticipantCount,
                        chatRoom.content,
                        chatRoom.isPrivate,
                        chatRoom.totalParticipantCount,
                        chatRoom.agreeCount,
                        chatRoom.disagreeCount,
                        chatRoom.createdAt,
                        chatRoom.closedAt,
                        chatRoom.onAir
                ))
                .from(chatRoom)
                .where(chatRoom.onAir.eq(true))
                .fetch();
    }

    @Override
    public List<ChatRoomResDto> findByRoomName(String keyword) {
        return null;
    }


}
