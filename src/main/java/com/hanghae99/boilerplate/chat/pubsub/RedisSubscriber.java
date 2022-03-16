package com.hanghae99.boilerplate.chat.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.chat.model.textchat.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    //    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;

    public void sendMessage(String publishMessage) {
        try {
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            log.info("RedisSubscriber - chatMassage: {}, {}", chatMessage.getMessage(), chatMessage.getType());
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);

        } catch (Exception e) {
            log.error("redis pub 요청이 있었으나 exception 발생: {}", e);
        }
    }


    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
     */
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        try {
//            // redis에서 발행된 데이터를 받아 deserialize
//            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
//            // ChatMessage 객채로 맵핑
//            ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
//            // Websocket 구독자에게 채팅 메시지 Send
//            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomId(), roomMessage);
//            log.info(roomMessage.getMessage());
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }
}
