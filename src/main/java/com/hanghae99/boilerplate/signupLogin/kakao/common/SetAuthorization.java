package com.hanghae99.boilerplate.signupLogin.kakao.common;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.RefreshTokenRedis;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.kakao.common.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SetAuthorization {

    @Autowired
    RefreshTokenRedis redis;
    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    ObjectMapper objectMapper;
    public void runIfloginSuccess(HttpServletResponse response, MemberContext memberContext) throws IOException {

        JwtToken accessToken = tokenFactory.createToken(memberContext, JwtConfig.tokenExpirationTime);
        JwtToken refreshToken = tokenFactory.createToken(memberContext,JwtConfig.refreshTokenExpTime);

        redis.setExpire(refreshToken.getToken(), memberContext.getUsername(),JwtConfig.refreshTokenExpTime);

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.addCookie(WebUtil.makeCookie(JwtConfig.AUTHENTICATION_HEADER_NAME, refreshToken.getToken()));
        response.setHeader(JwtConfig.AUTHENTICATION_HEADER_NAME, accessToken.getToken());

        objectMapper.writeValue(response.getWriter(), WebUtil.UserDataToMap(memberContext));
    }

}
