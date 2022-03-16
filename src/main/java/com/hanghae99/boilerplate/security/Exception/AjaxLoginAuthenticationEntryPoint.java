package com.hanghae99.boilerplate.security.Exception;

import antlr.Token;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.security.config.SecurityConfig;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//401에러 처리 해주는 부분
@Component
public class AjaxLoginAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
      objectMapper.writeValue(response.getWriter(), ExceptionResponse.of(HttpStatus.UNAUTHORIZED, authException.toString()));

    }
}
