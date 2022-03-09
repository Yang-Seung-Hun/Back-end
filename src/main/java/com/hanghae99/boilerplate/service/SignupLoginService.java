package com.hanghae99.boilerplate.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.model.Member;
import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
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
import java.util.HashMap;
import java.util.Map;

@Service
public class SignupLoginService {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    ObjectMapper objectMapper;
    @Transactional
    public void signupRequest(SignupReqestDto signupReqestDto) {
        boolean result = memberRepository.getEmail(signupReqestDto.getEmail()).isPresent();
        signupReqestDto.setPassword(passwordEncoder.encode(signupReqestDto.getPassword()));
        if (result)
            throw new IllegalArgumentException(signupReqestDto.getEmail() + " alread " + "Exist!");
        memberRepository.save(new Member(signupReqestDto));

    }

    public void logoutRequest(HttpServletRequest request, HttpServletResponse response,String email) throws IOException {
        refreshTokenRepository.deleteToken(email);

        JwtToken expiredToken = tokenFactory.createExpiredToken();
        Map<String, String> tokenMap = new HashMap<String, String>();
        tokenMap.put("access_token",expiredToken.getToken());
        objectMapper.writeValue(response.getWriter(), tokenMap);

        Cookie cookie = new Cookie("Authentitcation","null");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60* 24);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.setStatus(HttpStatus.OK.value());



    }
}
