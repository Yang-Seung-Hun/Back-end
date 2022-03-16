package com.hanghae99.boilerplate.security;


import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.RefreshTokenRedis;
import com.hanghae99.boilerplate.security.jwt.AccessToken;
import com.hanghae99.boilerplate.security.jwt.RawAccessToken;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.security.model.MemberContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RefreshTokenEndPoint {

    @Autowired
    JwtConfig jwtConfig;
    @Autowired
    TokenVerifier tokenVerifier;
    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    RefreshTokenRedis redis;

    public Jws<Claims> getJwtClimas(HttpServletRequest request) {
        try {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("Authorization") || redis.getData(cookie.getValue()) !=null){
                    Jws<Claims> jwsClaims = tokenVerifier.validateToken(cookie.getValue(), jwtConfig.getTokenSigningKey());
                    return jwsClaims;
                }
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
            return null;
        }
        return null;

    }

    //유효한 jws가 들어온다
    public Optional<MemberContext> getMemberContext(Jws<Claims> jws) {
            String email = jws.getBody().getSubject();
            List<String> scopes = jws.getBody().get("scopes", List.class);
            List<GrantedAuthority> authorityList = scopes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            MemberContext memberContext = MemberContext.create(email, authorityList);
            return Optional.of(memberContext);
    }


    public RawAccessToken setNewAccessToken(MemberContext memberContext, HttpServletResponse response) throws IOException {
        AccessToken accessToken = tokenFactory.createAccessToken(memberContext);
        response.setHeader(JwtConfig.AUTHENTICATION_HEADER_NAME,accessToken.getToken());
        return new RawAccessToken(accessToken.getToken());
    }


}
