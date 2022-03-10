package com.hanghae99.boilerplate.kakao.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.kakao.service.KakaoLoginService;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;

@RestController
public class KakaoLoginController {

    @Autowired
    KakaoLoginService kakaoLoginService;
    private final String URL = "kauth.kakao.com/oauth/authorize?client_id=91ee90dad2384a8f06ab7106b2f92daf&redirect_uri=http://localhost:8080/api/kakao/login&response_type=code";
    @Autowired
    ObjectMapper objectMapper;

    /**
     * @callback
     */
    @GetMapping("/api/kakao/login")
    public void kakaoLogin(HttpServletRequest request, HttpServletResponse response, @RequestParam String code) throws IOException {
        try {
            kakaoLoginService.getKakaoToken(response,code);
            return;
        }catch (Exception e){
            objectMapper.writeValue(response.getWriter(),
                    ExceptionResponse.of(HttpStatus.NOT_ACCEPTABLE,e.getMessage()));
        }

    }

}
