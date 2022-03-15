package com.hanghae99.boilerplate.chat.controller;

import com.hanghae99.boilerplate.chat.model.textchat.ChatMessage;
import com.hanghae99.boilerplate.chat.repository.RedisChatRoomRepository;
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

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    // 찬성과 반대가 room의 정보를 변화시켜야 하므로, repository 불러오기 / 그런데 누구를? redis를 하고 싶다.
    private final RedisChatRoomRepository redisChatRoomRepository;

    @MessageMapping("/chat/message")
    // 클라이언트에서는 prefix를 붙여서 /pub/chat/message 로 발행 요청을 하고, 이를 controller 가 받아서 처리
    // 메시지가 발행되면 /sub/chat/room/{roomId} 로 메시지를 send.
    // 해당 주소를 구독하고 있는 클라이언트에게 전달됨.
    // 구독자(subscriber)에 대한 구현은 따로 서버에서 할 필요가 없음 - 웹뷰에서 stomp 라이브러리를 이용해 subscriber 주소를 바라보고 있도록 하면 됨.
    public void message(ChatMessage chatMessage) {
        String roomId = chatMessage.getRoomId().toString();

        switch (chatMessage.getType()) {
            case ENTER:
                log.info("ENTER: {}", chatMessage.getSender());
                break;
            case AGREE:
                log.info("AGREE: {}", chatMessage.getSender());
                Long afterAgree = redisChatRoomRepository.addAgree(roomId);
                chatMessage.setAgreeCount(afterAgree);
                break;
            case CANCEL_AGREE:
                log.info("CANCEL_AGREE: {}", chatMessage.getSender());
                Long afterCancelAgree = redisChatRoomRepository.subAgree(roomId);
                chatMessage.setAgreeCount(afterCancelAgree);
                break;
            case DISAGREE:
                log.info("DISAGREE: {}", chatMessage.getSender());
                Long afterDisagree = redisChatRoomRepository.addDisagree(roomId);
                chatMessage.setDisagreeCount(afterDisagree);
                break;
            case CANCEL_DISAGREE:
                log.info("CANCEL_DISAGREE: {}", chatMessage.getSender());
                Long afterCancelDisagree = redisChatRoomRepository.subDisagree(roomId);
                chatMessage.setDisagreeCount(afterCancelDisagree);
                break;
            case LEAVE:
                log.info("LEAVE: {}", chatMessage.getSender());
                break;
        }

        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }


}