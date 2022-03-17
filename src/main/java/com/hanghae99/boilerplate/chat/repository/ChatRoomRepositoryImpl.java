package com.hanghae99.boilerplate.chat.repository;

import com.hanghae99.boilerplate.chat.model.dto.ChatRoomResDto;
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
    public List<ChatRoomResDto> findByKeyword(String keyword) {
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
                .where(chatRoom.roomName.contains(keyword)
                        .or(chatRoom.content.contains(keyword))) //todo 이건 원래 넣기로 하진 않았던 부분! 논의드려야함~ 왠지 실시간 방은 넣어도 될 것 같아서.
                .fetch();
    }


}
