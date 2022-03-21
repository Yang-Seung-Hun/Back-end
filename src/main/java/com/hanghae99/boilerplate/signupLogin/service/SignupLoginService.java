package com.hanghae99.boilerplate.signupLogin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.RefreshTokenRedis;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Service
public class SignupLoginService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RefreshTokenRedis redis;
    @Transactional

    public void signupRequest(SignupReqestDto signupReqestDto) {
        boolean result = memberRepository.existsMemberByEmail(signupReqestDto.getEmail());
        if (result) {
            log.info("{} is already exist",signupReqestDto.getEmail());
            throw new IllegalArgumentException(signupReqestDto.getEmail() + " already" + "Exist!");
        }
        signupReqestDto.setPassword(passwordEncoder.encode(signupReqestDto.getPassword()));
        memberRepository.save(new Member(signupReqestDto));
        log.info("{} ,nickname {} signup" ,signupReqestDto.getEmail(),signupReqestDto.getNickname());

    }

    public void logoutRequest(HttpServletRequest request) throws IOException {
        for (Cookie cookie :request.getCookies()){
           if( cookie.getName().equals("Authorization")){
               redis.removeData(cookie.getValue());
           }
        }
    }
}
