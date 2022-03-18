package com.hanghae99.boilerplate.signupLogin.kakao.service;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import com.hanghae99.boilerplate.signupLogin.kakao.common.Connection;
import com.hanghae99.boilerplate.signupLogin.kakao.common.KakaoUserData;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import javassist.tools.web.BadHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.io.*;

@Service
@Slf4j
public class KakaoLoginService {

    @Autowired
    Connection connection;


    @JsonIgnoreProperties
    public TemporaryUser getKakaoUserInformation(String code) throws IOException {
        if(code == null || code.isBlank())
            throw new AuthenticationException("bad request");
        KakaoUserData user = connection.getaccessToken(code);
        TemporaryUser temporaryUser = connection.getUserData(user.getAccess_token());
        log.info("{} login kakao ", temporaryUser.getEmail());
        return temporaryUser;
    }
}

