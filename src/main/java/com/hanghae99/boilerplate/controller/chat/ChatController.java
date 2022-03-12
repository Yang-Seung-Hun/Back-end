package com.hanghae99.boilerplate.controller.chat;

import com.hanghae99.boilerplate.model.chat.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@RequiredArgsConstructor
@Slf4j
@Controller
@CrossOrigin
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat/message")
    // 클라이언트에서는 prefix를 붙여서 /pub/chat/message 로 발행 요청을 하고, 이를 controller 가 받아서 처리
    // 메시지가 발행되면 /sub/chat/room/{roomId} 로 메시지를 send.
    // 해당 주소를 구독하고 있는 클라이언트에게 전달됨.
    // 구독자(subscriber)에 대한 구현은 따로 서버에서 할 필요가 없음 - 웹뷰에서 stomp 라이브러리를 이용해 subscriber 주소를 바라보고 있도록 하면 됨.
    public void message(ChatMessage message) {
        //오.. 근데 prefix로 이미 sub 을 config 에서 달지 않았나..? 중복되는 건 아니려나.
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        log.info("받은 chatMessage의 sender는: {}", message.getSender());
    }
}