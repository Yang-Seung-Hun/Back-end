package com.hanghae99.boilerplate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 메시지 구독 요청의 prefix
        config.setApplicationDestinationPrefixes("/pub");  // 메시지 발행 요청의 prefix
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp webSocket 연결의 endpoint
        // -> 개발 서버의 접속 주소: ws://localhost:8080/ws-stomp
        // cors 대응을 위한 시도..
        registry.addEndpoint("/ws-stomp").setAllowedOriginPatterns("*")
                .withSockJS().setSupressCors(true);
    }
}
