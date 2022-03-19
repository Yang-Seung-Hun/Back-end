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
//
//
//
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
//                        chatRoom.entries.
////                        chatRoom.entries.size().as("")
//                ))
//                .from(chatRoom)
//                .where(chatRoom.roomName.contains(keyword)
////                        .or(chatRoom.content.contains(keyword))) //todo 이건 원래 넣기로 하진 않았던 부분! 논의드려야함~ 왠지 실시간 방은 넣어도 될 것 같아서.
//                .fetch();
//    }


}
