package com.hanghae99.boilerplate.security.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class JwtConfig {
    private final Integer tokenExpirationTime = 60; //60분
    private Integer refreshTokenExpTime=120; //2시간
    private String tokenIssuer = "http://choonsik2.site"; //토큰 소유자 정보
    private String tokenSigningKey="1234"; //  임시 암호화 키

    private String expireSignKey="1111";


}
