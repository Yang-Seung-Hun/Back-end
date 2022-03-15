package com.hanghae99.boilerplate.chat.controller;

import com.hanghae99.boilerplate.chat.model.textchat.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@RequiredArgsConstructor
@Slf4j
@Controller
@CrossOrigin
public class ChatController {

    //    private final SimpMessageSendingOperations messagingTemplate; //when using only stomp
//    private final RedisChatRoomRepository redisChatRoomRepository;
    //    private final RedisPublisher redisPublisher;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    @MessageMapping("/chat/message")
    // 클라이언트에서는 prefix를 붙여서 /pub/chat/message 로 발행 요청을 하고, 이를 controller 가 받아서 처리
    // 메시지가 발행되면 /sub/chat/room/{roomId} 로 메시지를 send.
    // 해당 주소를 구독하고 있는 클라이언트에게 전달됨.
    // 구독자(subscriber)에 대한 구현은 따로 서버에서 할 필요가 없음 - 웹뷰에서 stomp 라이브러리를 이용해 subscriber 주소를 바라보고 있도록 하면 됨.
    public void message(ChatMessage chatMessage) {

        // todo 헤더의 토큰을 통한 사용자 식별이 가능할 것. chatMessage 에 명시된 사용자와 같은지 체크해 볼 수도 있을 것.
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
//            redisChatRoomRepository.enterChatRoom(chatMessage.getRoomId());
        }

        //(1) only stomp
//        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        //(2) stomp + redis
//        redisPublisher.publish(redisChatRoomRepository.getTopic(chatMessage.getRoomId().toString()), chatMessage);

        //(3) with redisTemplate directly
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

    //
//    //todo 찬반투표라는 pub -> 해당 sub 에 투표현황 send
    @MessageMapping("/chat/vote")
    public void vote() {

    }

}