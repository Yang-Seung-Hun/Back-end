package com.hanghae99.boilerplate.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.Exception.ExceptionResponse;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.SecurityConfig;
import com.hanghae99.boilerplate.security.jwt.JwtAuthenticationToken;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.security.model.RefreshTokenDB;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier.HEADER_PREFIX;


@RestController
public class RefreshTokenEndPoint {

    @Autowired
    JwtConfig jwtConfig;
    @Autowired
    TokenVerifier tokenVerifier;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    TokenFactory tokenFactory;

    //여기로 들어오는건 리프레시 토큰이 온다
    @GetMapping("/api/token")
    @Transactional

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();

        String email = new String();
        String header = request.getHeader(SecurityConfig.AUTHENTICATION_HEADER_NAME);
        if (header == null || header.isBlank()) {
            objectMapper.writeValue(response.getWriter(), ExceptionResponse.of(HttpStatus.BAD_REQUEST, "token is not null and not  balnk "));
            return;
        }
        try {
            String token = header.substring(HEADER_PREFIX.length(), header.length());
            Jws<Claims> jwsClaims = tokenVerifier.validateToken(token, jwtConfig.getTokenSigningKey());
            email = jwsClaims.getBody().getSubject();
            Optional<RefreshTokenDB> refreshToken = refreshTokenRepository.findById(email);
            if (!refreshToken.isPresent()) {
                throw new AuthenticationException("please login!!");
            }
            if (!token.equals(refreshToken.get().getToken())) {
                throw new AuthenticationException("pleas login!");
            }



            List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
            List<GrantedAuthority> authorityList = scopes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            Map<String, String> tokenMap = new HashMap<String, String>();

            MemberContext context = MemberContext.create(email, authorityList);
            tokenMap.put("access_token", tokenFactory.createAccessToken(context).getToken());
            response.setStatus(HttpStatus.OK.value());
            objectMapper.writeValue(response.getWriter(), tokenMap);



        } catch (ExpiredJwtException | AuthenticationException e) {

            refreshTokenRepository.deleteToken(email);
            objectMapper.writeValue(response.getWriter(), ExceptionResponse.of(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (Exception e) {
            objectMapper.writeValue(response.getWriter(), ExceptionResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));

        }
    }


}
