//package com.hanghae99.boilerplate.chat.pubsub;
//
//import com.hanghae99.boilerplate.chat.model.textchat.ChatMessage;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.stereotype.Service;
//
//@RequiredArgsConstructor
//@Service
//public class RedisPublisher {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public void publish(ChannelTopic topic, ChatMessage message) {
//        redisTemplate.convertAndSend(topic.getTopic(), message);
//    }
//}
