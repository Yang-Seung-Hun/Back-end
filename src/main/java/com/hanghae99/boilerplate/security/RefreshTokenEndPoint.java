package com.hanghae99.boilerplate.security;


import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.SecurityConfig;
import com.hanghae99.boilerplate.security.jwt.JwtAuthenticationToken;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.security.model.MemberContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class RefreshTokenEndPoint {

    @Autowired
    JwtConfig jwtConfig;
    @Autowired
    TokenVerifier tokenVerifier;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @GetMapping("/auth/token")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String msg = new String();
        String token = request.getHeader(SecurityConfig.AUTHENTICATION_HEADER_NAME);
        if (token == null || token.isBlank()) {
            msg = "token is null or blank";
            return ResponseEntity.badRequest().body(msg);
        }
        try {
            Jws<Claims> jwsClaims = tokenVerifier.validateToken(token, jwtConfig.getTokenSigningKey());
            String sub = jwsClaims.getBody().getSubject();
            List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
            List<GrantedAuthority> authorityList = scopes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            MemberContext context = MemberContext.create(sub, authorityList);
            refreshTokenRepository.deleteById(context.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(new JwtAuthenticationToken(context, context.getAuthorities()));

        } catch (Exception e) {
            msg = e.getMessage();
           return  ResponseEntity.badRequest().body(msg);
        }
    }


}
