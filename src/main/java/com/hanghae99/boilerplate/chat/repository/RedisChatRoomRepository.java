package com.hanghae99.boilerplate.chat.repository;


import com.hanghae99.boilerplate.chat.model.ChatRoom;
import com.hanghae99.boilerplate.chat.pubsub.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class RedisChatRoomRepository {
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS);
    }
    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    /*
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장
     */
    public ChatRoom createChatRoom(String roomId, ChatRoom chatRoom) {
        opsHashChatRoom.put(CHAT_ROOMS, roomId, chatRoom);
        return chatRoom;
    }

    /*
     * 채팅방 입장 : redis 에 topic 을 만들고 pub/sub 통신을 하기 위해 리스너를 설정
     */
    public void enterChatRoom(Long roomId) {
        ChannelTopic topic = topics.get(roomId.toString());
        if (topic == null)
//            topic = new ChannelTopic(roomId);
            // todo : redis 의 channelTopic은 String밖에 안되는 건가..??
            topic = new ChannelTopic(roomId.toString());
        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(roomId.toString(), topic);
    }

    /*
     * 채팅방 종료(MODERATOR에 의해) redis hash 에서 제거
     */



    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }
}
