package com.hanghae99.boilerplate.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RefreshTokenRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String getData(String key){
        ValueOperations<String,String> valueOperations=stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setData(String key,String value){
        ValueOperations<String,String>valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key,value);
    }

    public void setExpire(String key,String value,long expireTime){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        Duration duration = Duration.ofSeconds(expireTime);
        valueOperations.set(key,value,duration);
    }
    public void removeData(String key){
        stringRedisTemplate.delete(key);
    }

}