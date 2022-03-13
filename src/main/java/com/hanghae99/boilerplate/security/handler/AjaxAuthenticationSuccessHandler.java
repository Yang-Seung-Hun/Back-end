package com.hanghae99.boilerplate.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.config.Redis;
import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private MemberRepository memberRepository;


    private TokenFactory tokenFactory;

    private ObjectMapper objectMapper;

    private JwtConfig jwtConfig;


    private Redis redis;

    public AjaxAuthenticationSuccessHandler(TokenFactory tokenFactory, MemberRepository memberRepository, ObjectMapper objectMapper, Redis redis,
                                            JwtConfig jwtConfig) {
        this.memberRepository = memberRepository;
        this.tokenFactory = tokenFactory;
        this.objectMapper = objectMapper;
        this.redis = redis;
        this.jwtConfig = jwtConfig;
    }


    //인증 성공시 호출된다  //name이 null일 일이 없다
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        MemberContext memberContext = (MemberContext) authentication.getPrincipal();

        JwtToken accessToken = tokenFactory.createAccessToken(memberContext);
        JwtToken refreshToken = tokenFactory.createRefreshToken(memberContext);


        Map<String, String> tokenMap = new HashMap<String, String>();


        Cookie cookie = new Cookie("Authorization", refreshToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24);
        cookie.setPath("/");
        response.addCookie(cookie);

        response.setHeader("Authorization", accessToken.getToken());


        Optional<String> nickname = memberRepository.getNickname(memberContext.getUsername());
        nickname.ifPresent(s -> tokenMap.put("nickname", s));
        tokenMap.put("email", memberContext.getUsername());

        objectMapper.writeValue(response.getWriter(), tokenMap);

        log.info("{} login success", memberContext.getUsername());

        //redis에 refresh token 을 key로  저장
        redis.setExpire(refreshToken.getToken(), memberContext.getUsername(), jwtConfig.getRefreshTokenExpTime());

        log.info("{} Authentication success",memberContext.getUsername());
    }

}
