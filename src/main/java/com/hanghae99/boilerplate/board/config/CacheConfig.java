package com.hanghae99.boilerplate.board.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableCaching
public class CacheConfig {

    //private final RedisProperties redisProperties;

//    @Value("${spring.redis.cache.host}")
//    private String redisHost;
//
//    @Value("${spring.redis.cache.port}")
//    private int redisPort;

    //RedisConnectionFactory redisConnectionFactory;
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
//    }

    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RedisConnectionFactory connectionFactory;



    private final int DEFAULT_EXPIRE_SECONDS = 1;

    private final String ApiAccessInfo = "board";
    private final int API_ACCESS_INFO_EXPIRE_SECONDS = 600;

    private final String Comments = "Comment";
    private final int Comments_EXPIRE_SECONDS = 3600;

    private ObjectMapper objectMapper() {
        // jackson 2.10이상 3.0버전까지 적용 가능
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        return JsonMapper.builder()
                .polymorphicTypeValidator(ptv)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .addModule(new JavaTimeModule())
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
                .build();
    }

    @Bean //스프링 로컬 캐시가 아닌 Redis 서버에 저장
    public CacheManager redisCacheManager(){
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                //.entryTtl(Duration.ofSeconds(DEFAULT_EXPIRE_SECONDS))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())))
                ;

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // ApiAccessInfo
        cacheConfigurations.put(ApiAccessInfo, RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(API_ACCESS_INFO_EXPIRE_SECONDS)));


        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }


}
