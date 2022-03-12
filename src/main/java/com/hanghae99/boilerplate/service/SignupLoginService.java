package com.hanghae99.boilerplate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.config.Redis;
import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.model.Member;
import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SignupLoginService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Redis redis;
    @Transactional
    public void signupRequest(SignupReqestDto signupReqestDto) {
        boolean result = memberRepository.getEmail(signupReqestDto.getEmail()).isPresent();
        if (result) {
            log.info("{} is already exist",signupReqestDto.getEmail());
            throw new IllegalArgumentException(signupReqestDto.getEmail() + " already" + "Exist!");
        }
        signupReqestDto.setPassword(passwordEncoder.encode(signupReqestDto.getPassword()));
        memberRepository.save(new Member(signupReqestDto));
        log.info("{} ,nickname {} signup" ,signupReqestDto.getEmail(),signupReqestDto.getNickname());

    }

    public void logoutRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

        for (Cookie cookie :request.getCookies()){
           if( cookie.getName().equals("Authorization")){
               redis.removeData(cookie.getValue());
           }


        }
    }
}
