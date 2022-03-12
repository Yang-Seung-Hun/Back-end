package com.hanghae99.boilerplate.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.SecurityConfig;
import com.hanghae99.boilerplate.security.jwt.AccessToken;
import com.hanghae99.boilerplate.security.jwt.JwtAuthenticationToken;
import com.hanghae99.boilerplate.security.jwt.RawAccessToken;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenExtractor;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.security.model.RefreshTokenDB;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;
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
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    TokenExtractor tokenExtractor;


    public Jws<Claims> getJwtClimas(HttpServletRequest request) {
        try {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("Authorization")) {
                    Jws<Claims> jwsClaims = tokenVerifier.validateToken(cookie.getValue(), jwtConfig.getTokenSigningKey());
                    return jwsClaims;
                }
            }
        } catch (Exception e) {
            log.debug("{}", e.getMessage());
            return null;
        }
        log.debug("request.getCookies is empty or cookie.getName not matche Authentitcation");
        return null;
    }

    @Transactional(readOnly = true)
    public Optional<MemberContext> getMemberContext(Jws<Claims> jws) {
        try {
            String email = jws.getBody().getSubject();
            Optional<RefreshTokenDB> refreshToken = refreshTokenRepository.findById(email);
            List<String> scopes = jws.getBody().get("scopes", List.class);
            List<GrantedAuthority> authorityList = scopes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            MemberContext memberContext = MemberContext.create(email, authorityList);
            return Optional.of(memberContext);
        } catch (Exception e) {
            log.debug("{} ", e.getMessage());
            return Optional.empty();
        }
    }


    public RawAccessToken setNewAccessToken(MemberContext memberContext, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> tokenMap = new HashMap<String, String>();
        AccessToken accessToken = tokenFactory.createAccessToken(memberContext);
        response.setHeader("Authorization",accessToken.getToken());
        return new RawAccessToken(accessToken.getToken());
    }


}