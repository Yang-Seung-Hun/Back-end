//package com.hanghae99.boilerplate.chat.config;
//
//import com.hanghae99.boilerplate.chat.pubsub.RedisSubscriber;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
//@RequiredArgsConstructor
//@Configuration
//public class RedisSentinelConfig {
//
//    private final RedisProperties redisProperties;
//
//    @Value("${spring.redis.port}")
//    public int sentinelPort;
//
//    @Value("${spring.redis.sentinel.master}")
//    public String masterName;
//
//    @Value("${spring.redis.sentinel.password}")
//    public String password;
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//
//        org.springframework.data.redis.connection.RedisSentinelConfiguration redisSentinelConfiguration = new org.springframework.data.redis.connection.RedisSentinelConfiguration()
//                .master(masterName)
//                .sentinel("13.125.163.139", sentinelPort)
//                .sentinel("3.37.114.211", sentinelPort)
//                .sentinel("3.37.154.5", sentinelPort);
//
//        redisSentinelConfiguration.setPassword(password);
//        return new LettuceConnectionFactory(redisSentinelConfiguration);
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        final RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class)); //standalone 과의 template diff 는 여기
//        redisTemplate.setConnectionFactory(redisConnectionFactory());
//        return redisTemplate;
//    }
//
//    // add previous setting for pub/sub structure
//    @Bean
//    public ChannelTopic channelTopic() {
//        return new ChannelTopic("chatroom");
//    }
//
//    @Bean
//    public RedisMessageListenerContainer redisMessageListener(
//                                                              MessageListenerAdapter listenerAdapter,
//                                                              ChannelTopic channelTopic) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(this.redisConnectionFactory());
//        container.addMessageListener(listenerAdapter, channelTopic);
//        return container;
//    }
//
//    @Bean
//    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
//        return new MessageListenerAdapter(subscriber, "sendMessage");
//    }
//
//}
