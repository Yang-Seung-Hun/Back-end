package com.hanghae99.boilerplate.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ChatRoomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.queryFactory = jpaQueryFactory;
    }

//    @Override
//    public List<ChatRoomRedisDto> findOnAirChatRooms() {
//        return queryFactory
//                .select(new QChatRoomRedisDto(
//                        chatRoom.roomId,
//                        chatRoom.roomName,
//                        chatRoom.category,
//                        chatRoom.moderator.id.as("moderatorId"),
//                        chatRoom.moderator.nickname.as("moderatorNickname"),
//                        chatRoom.maxParticipantCount,
//                        chatRoom.content,
//                        chatRoom.isPrivate,
//                        chatRoom.parti
//                        chatRoom.entries.size().as("")
//                ))
//                .from(chatRoom)
//                .where(chatRoom.onAir.eq(true))
//                .fetch();
//    }

//    @Override
//    public List<ChatRoomRedisDto> findByKeyword(String keyword) {
//        return queryFactory
//                .select(new QChatRoomRedisDto(
//                        chatRoom.roomId,
//                        chatRoom.roomName,
//                        chatRoom.category,
//                        chatRoom.moderator.id.as("moderatorId"),
//                        chatRoom.moderator.nickname.as("moderatorNickname"),
//                        chatRoom.maxParticipantCount,
//                        chatRoom.content,
//                        chatRoom.isPrivate,
//                        // participantsIds
//                        chatRoom.entries.
////                        chatRoom.entries.size().as("")
//                ))
//                .from(chatRoom)
//                .where(chatRoom.roomName.contains(keyword)
//                        .and(chatRoom.onAir.eq(false)))
//                .fetch();
//    }
}
