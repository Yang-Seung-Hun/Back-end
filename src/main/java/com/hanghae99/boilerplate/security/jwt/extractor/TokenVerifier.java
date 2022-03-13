package com.hanghae99.boilerplate.security.jwt.extractor;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.jwt.JwtAuthenticationToken;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultJws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class TokenVerifier {

    @Autowired
    JwtConfig jwtConfig;
    public Jws<Claims> validateToken(String jwtToken, String secretKey) {

        if (jwtToken == null || jwtToken.isBlank()) {
            throw new AuthenticationServiceException("Authorization  cannot be blank");
        }

//      String  jwtToken = header.substring(HEADER_PREFIX.length(), header.length());
        Jws<Claims> claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return claims;
        } catch (UnsupportedJwtException|MalformedJwtException|SignatureException|IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        }  catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Jwt Expired !!");
        }

    }




}