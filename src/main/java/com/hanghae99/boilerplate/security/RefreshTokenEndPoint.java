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
    TokenVerifier tokenVerifier;
    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    RefreshTokenRedis redis;
//        Arrays.stream(request.getCookies()).anyMatch(cookie -> removeCookieIfSame(cookie));

    public Jws<Claims> getJwtClimas(HttpServletRequest request) {
        try {
            Optional<Cookie> cookie = Arrays.stream(request.getCookies()).filter(c -> isOkGiveClaims(c)).findFirst();
            if (cookie.isPresent())
                return tokenVerifier.validateToken(cookie.get().getValue(), JwtConfig.tokenSigningKey);

        } catch (Exception e) {

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

        MemberContext memberContext = MemberContext.create(email, authorityList, jws.getBody().getIssuer(), Long.valueOf(jws.getBody().getAudience()));
        return Optional.of(memberContext);

    }


    public RawAccessToken setNewAccessToken(MemberContext memberContext, HttpServletResponse response) throws IOException {
        AccessToken accessToken = tokenFactory.createAccessToken(memberContext);
        response.setHeader(JwtConfig.AUTHENTICATION_HEADER_NAME, accessToken.getToken());
        return new RawAccessToken(accessToken.getToken());
    }

    private boolean isOkGiveClaims(Cookie cookie) {
        return (cookie.getName().equals(JwtConfig.AUTHENTICATION_HEADER_NAME) || redis.getData(cookie.getValue()) != null);
    }
}
