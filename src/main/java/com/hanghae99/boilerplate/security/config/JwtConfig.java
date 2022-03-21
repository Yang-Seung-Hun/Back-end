package com.hanghae99.boilerplate.security.config;




public class JwtConfig {
    public final static  Integer tokenExpirationTime = 1200; //60분
    public final static Integer refreshTokenExpTime=3000; //2시간
    public final static  String tokenIssuer = "http://choonsik2.site"; //토큰 소유자 정보
    public final static  String tokenSigningKey="1234"; //  임시 암호화 키



    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";

    public static final String TOKEN_TYPE   = "Bearer ";

}
