package com.hanghae99.boilerplate.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.RefreshTokenRedis;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.kakao.common.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    private TokenFactory tokenFactory;

    private ObjectMapper objectMapper;




    private RefreshTokenRedis redis;

    public AjaxAuthenticationSuccessHandler(TokenFactory tokenFactory , ObjectMapper objectMapper, RefreshTokenRedis redis) {
        this.tokenFactory = tokenFactory;
        this.objectMapper = objectMapper;
        this.redis = redis;

    }


    //인증 성공시 호출된다  //name이 null일 일이 없다
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        MemberContext memberContext = (MemberContext) authentication.getPrincipal();

        JwtToken accessToken = tokenFactory.createToken(memberContext,JwtConfig.tokenExpirationTime);
        JwtToken refreshToken = tokenFactory.createToken(memberContext,JwtConfig.refreshTokenExpTime);


        response.addCookie(WebUtil.makeCookie(JwtConfig.AUTHENTICATION_HEADER_NAME, refreshToken.getToken()));

        response.setHeader(JwtConfig.AUTHENTICATION_HEADER_NAME, accessToken.getToken());


        objectMapper.writeValue(response.getWriter(), WebUtil.UserDataToMap(memberContext));

        //redis에 refresh token 을 key로  저장
        redis.removeData(refreshToken.getToken());
        redis.setExpire(refreshToken.getToken(), memberContext.getUsername(), JwtConfig.refreshTokenExpTime);


    }

}
