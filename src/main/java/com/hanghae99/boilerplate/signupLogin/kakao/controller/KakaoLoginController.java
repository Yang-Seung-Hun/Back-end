package com.hanghae99.boilerplate.signupLogin.kakao.controller;


import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.kakao.common.SetAuthorization;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import com.hanghae99.boilerplate.signupLogin.kakao.common.RegisterMember;
import com.hanghae99.boilerplate.signupLogin.kakao.service.KakaoLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j

public class KakaoLoginController {

    @Autowired
    KakaoLoginService kakaoLoginService;

    private final String URL = "kauth.kakao.com/oauth/authorize?client_id=91ee90dad2384a8f06ab7106b2f92daf&redirect_uri=http://localhost:3000/api/kakao/login&response_type=code";

    @Autowired
    RegisterMember registerMember;
    @Autowired
    SetAuthorization setAuthorization;

    @GetMapping("/api/kakao/login")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
        TemporaryUser temporaryUser = kakaoLoginService.getKakaoUserInformation(code);
        MemberContext memberContext = registerMember.registerKakaoUserToMember(temporaryUser);
        setAuthorization.runIfloginSuccess(response,memberContext);

    }
}


