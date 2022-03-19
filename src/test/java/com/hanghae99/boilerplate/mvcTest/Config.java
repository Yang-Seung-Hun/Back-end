package com.hanghae99.boilerplate.mvcTest;


import com.carrotsearch.hppc.ObjectByteMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.config.RefreshTokenRedis;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.service.UserDetailsImpl;
import com.hanghae99.boilerplate.security.service.UserDetailsServiceImpl;
import com.hanghae99.boilerplate.signupLogin.kakao.common.Connection;
import com.hanghae99.boilerplate.signupLogin.kakao.common.RegisterMember;
import com.hanghae99.boilerplate.signupLogin.kakao.service.KakaoLoginService;
import com.hanghae99.boilerplate.signupLogin.service.SignupLoginService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class Config {

    @MockBean
    SignupLoginService signupLoginService;
    @MockBean
    KakaoLoginService kakaoLoginService;
    @MockBean
    UserDetailsServiceImpl  userDetails;
    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    RegisterMember registerMember;


    @MockBean
    RefreshTokenRedis redis;



    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TokenFactory tokenFactory;



}
