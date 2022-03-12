package com.hanghae99.boilerplate.kakao.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.kakao.service.KakaoLoginService;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@Slf4j

public class KakaoLoginController {

    @Autowired
    KakaoLoginService kakaoLoginService;
//    private final String URL ="kauth.kakao.com/oauth/authorize?client_id=91ee90dad2384a8f06ab7106b2f92daf&redirect_uri=http://localhost:3000/api/kakao/login&response_type=code";
//    private final String URL = "kauth.kakao.com/oauth/authorize?client_id=91ee90dad2384a8f06ab7106b2f92daf&redirect_uri=http://18.117.124.131/api/kakao/login&response_type=code";
//        private final String URL = "kauth.kakao.com/oauth/authorize?client_id=91ee90dad2384a8f06ab7106b2f92daf&redirect_uri=http://localhost:8080/api/kakao/login&response_type=code";
//private final  String URL =  "https://kauth.kakao.com/oauth/authorize?client_id=61d258f52ee7b081b616d4119c86ba99&redirect_uri=http://localhost:3000/oauth/kakao/callback&response_type=code";
    @Autowired
    ObjectMapper objectMapper;

    /**
     * @callback
     */
    @GetMapping("/api/kakao/login")
    public void  kakaoLogin(HttpServletRequest request, HttpServletResponse response, @RequestHeader String Authorization) throws IOException {
        log.info("request TOKKEN - {}",Authorization);
        try {
            kakaoLoginService.getKakaoUserInformaiton(response,Authorization);

        }catch (Exception e){
            log.info(e.getMessage());
            objectMapper.writeValue(response.getWriter(),
                    ExceptionResponse.of(HttpStatus.NOT_ACCEPTABLE,e.getMessage()));
        }

    }

}
