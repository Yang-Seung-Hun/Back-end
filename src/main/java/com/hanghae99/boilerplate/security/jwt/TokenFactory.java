package com.hanghae99.boilerplate.security.jwt;


import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenFactory {


    public AccessToken createAccessToken(MemberContext memberContext) {
        Claims claims = Jwts.claims().setSubject(memberContext.getUsername());
        claims.put("scopes", memberContext.getAuthorities().stream().map(Authority ->
                Authority.toString()).collect(Collectors.toList()));

        LocalDateTime cur = LocalDateTime.now();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer( String.valueOf(memberContext.getMemberId())) // Issuer : member ID
                .setAudience(memberContext.getNickname())                //Audience :member nickname
                .setIssuedAt(Date.from(cur.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(cur
                        .plusMinutes(JwtConfig.tokenExpirationTime)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, JwtConfig.tokenSigningKey)
                .compact();
        return new AccessToken(token, claims);
    }

        public JwtToken createRefreshToken(MemberContext memberContext) {

            LocalDateTime currentTime = LocalDateTime.now();

            Claims claims = Jwts.claims().setSubject(memberContext.getUsername());
            claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));

            String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuer( String.valueOf(memberContext.getMemberId())) // Issuer : member ID
                    .setAudience(memberContext.getNickname())                //Audience :member nickname
                    .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                    .setExpiration(Date.from(currentTime
                            .plusDays(JwtConfig.refreshTokenExpTime)
                            .atZone(ZoneId.systemDefault()).toInstant()))
                    .signWith(SignatureAlgorithm.HS512, JwtConfig.tokenSigningKey)
                    .compact();

            return new AccessToken(token, claims);
        }



}
