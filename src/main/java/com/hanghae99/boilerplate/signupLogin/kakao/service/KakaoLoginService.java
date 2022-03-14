package com.hanghae99.boilerplate.signupLogin.kakao.service;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.config.Redis;
import com.hanghae99.boilerplate.security.jwt.RawAccessToken;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.signupLogin.dto.responseDto.LoginResponseDto;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import com.hanghae99.boilerplate.signupLogin.kakao.common.Connection;
import com.hanghae99.boilerplate.signupLogin.kakao.common.KakaoUserData;
import com.hanghae99.boilerplate.signupLogin.kakao.common.RegisterMember;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KakaoLoginService {

    @Autowired
    Connection connection;
    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    RegisterMember registerMember;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    Redis redis;

    @Autowired
    TokenVerifier tokenVerifier;
    @Autowired
    JwtConfig jwtConfig;

    @JsonIgnoreProperties
    public TemporaryUser getKakaoUserInformation(String code) throws IOException {

        KakaoUserData user = connection.getaccessToken(code);
        TemporaryUser temporaryUser = connection.getUserData(user.getAccess_token());
        log.info("{} login kakao ", temporaryUser.getEmail());
        return temporaryUser;
    }
}

