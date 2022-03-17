package com.hanghae99.boilerplate.signupLogin.kakao.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.RefreshTokenRedis;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.dto.responseDto.LoginResponseDto;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import com.hanghae99.boilerplate.signupLogin.kakao.common.RegisterMember;
import com.hanghae99.boilerplate.signupLogin.kakao.service.KakaoLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j

public class KakaoLoginController {

    @Autowired
    KakaoLoginService kakaoLoginService;

    private final String URL ="kauth.kakao.com/oauth/authorize?client_id=91ee90dad2384a8f06ab7106b2f92daf&redirect_uri=http://localhost:3000/api/kakao/login&response_type=code";
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    RegisterMember registerMember;
    @Autowired
    RefreshTokenRedis redis;
    @Autowired
    JwtConfig jwtConfig;
    /**
     * @return
     * @callback
     */
    @GetMapping("/api/kakao/login")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
        log.info("kakao-TOKKEN : {}", code);
        TemporaryUser temporaryUser = kakaoLoginService.getKakaoUserInformation(code);
        MemberContext memberContext = registerMember.registerKakaoUserToMember(temporaryUser);

        JwtToken accessToken = tokenFactory.createAccessToken(memberContext);
        JwtToken refreshToken = tokenFactory.createRefreshToken(memberContext);

        redis.setExpire(refreshToken.getToken(),memberContext.getUsername(), jwtConfig.getRefreshTokenExpTime());

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());

        Cookie cookie = new Cookie(JwtConfig.AUTHENTICATION_HEADER_NAME,refreshToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60* 48);
        cookie.setPath("/");
        response.addCookie(cookie);


        Map<String,String> userData= new HashMap<>();
        userData.put("profileImageUrl",temporaryUser.getProfileImageUrl());
        userData.put("nickname",temporaryUser.getNickname());
        userData.put(JwtConfig.AUTHENTICATION_HEADER_NAME,accessToken.getToken());
        response.setHeader(JwtConfig.AUTHENTICATION_HEADER_NAME,accessToken.getToken());
        objectMapper.writeValue(response.getWriter(),userData);


    }
}


