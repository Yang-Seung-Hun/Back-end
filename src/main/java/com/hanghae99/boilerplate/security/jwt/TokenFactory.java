package com.hanghae99.boilerplate.security.jwt;


import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenFactory {

    @Autowired
    private JwtConfig jwtConfig;

    public AccessToken createAccessToken(MemberContext memberContext) {
        if (memberContext.getUsername().isBlank()) {
            throw new IllegalArgumentException("Cannot create JWT Token ,username is empty");
        }

        Claims claims = Jwts.claims().setSubject(memberContext.getUsername());
        claims.put("scopes", memberContext.getAuthorities().stream().map(Authority ->
                Authority.toString()).collect(Collectors.toList()));

        LocalDateTime cur = LocalDateTime.now();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(jwtConfig.getTokenIssuer())
                .setIssuedAt(Date.from(cur.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(cur
                        .plusMinutes(jwtConfig.getTokenExpirationTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getTokenSigningKey())
                .compact();
        return new AccessToken(token, claims);
    }

        public JwtToken createRefreshToken(MemberContext memberContext) {
            if (memberContext.getUsername().isBlank()) {
                throw new IllegalArgumentException("Cannot create JWT Token without username");
            }

            LocalDateTime currentTime = LocalDateTime.now();

            Claims claims = Jwts.claims().setSubject(memberContext.getUsername());
            claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuer(jwtConfig.getTokenIssuer())
                    .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                    .setExpiration(Date.from(currentTime
                            .plusMinutes(jwtConfig.getRefreshTokenExpTime())
                            .atZone(ZoneId.systemDefault()).toInstant()))
                    .signWith(SignatureAlgorithm.HS512, jwtConfig.getTokenSigningKey())
                    .compact();

            return new AccessToken(token, claims);
        }
    public JwtToken createExpiredToken() {


        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().setSubject("$$$$");

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(jwtConfig.getTokenIssuer())
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512,jwtConfig.getExpireSignKey())
                .compact();

        return new AccessToken(token, claims);
    }

}
