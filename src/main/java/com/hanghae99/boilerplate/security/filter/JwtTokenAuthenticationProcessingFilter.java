package com.hanghae99.boilerplate.security.filter;

import com.hanghae99.boilerplate.security.RefreshTokenEndPoint;
import com.hanghae99.boilerplate.security.config.SecurityConfig;
import com.hanghae99.boilerplate.security.jwt.JwtAuthenticationToken;
import com.hanghae99.boilerplate.security.jwt.RawAccessToken;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenExtractor;
import com.hanghae99.boilerplate.security.model.MemberContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;


//jwt로 인가를 검증해주는 필터
public class JwtTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private AuthenticationFailureHandler failureHandler;

    private TokenExtractor tokenExtractor;

    private TokenFactory tokenFactory;

    private RefreshTokenEndPoint refreshTokenEndPoint;

    public JwtTokenAuthenticationProcessingFilter(AuthenticationFailureHandler failureHandler,
                                                  TokenExtractor tokenExtractor
            , RequestMatcher matcher,RefreshTokenEndPoint refreshTokenEndPoint) {

        super(matcher);
        this.failureHandler = failureHandler;
        this.tokenExtractor = tokenExtractor;
        this.refreshTokenEndPoint= refreshTokenEndPoint;
    }


    //매니저에게 검증을 맞긴다
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String payload = request.getHeader(SecurityConfig.AUTHENTICATION_HEADER_NAME);
        RawAccessToken token = new RawAccessToken(tokenExtractor.extract(payload));
        try {
            Authentication authentication = getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
        } catch (ExpiredJwtException e) {
            Optional<Jws<Claims>> jwt = refreshTokenEndPoint.getJwtClimas(request);

            if (jwt.isEmpty())
                throw new javax.security.sasl.AuthenticationException();
            Optional<MemberContext> memberContext = refreshTokenEndPoint.getMemberContext(jwt.get());
            if (memberContext.isEmpty())
                throw new javax.security.sasl.AuthenticationException();

            token =refreshTokenEndPoint.setNewAccessToken(memberContext.get(), response);
            return new JwtAuthenticationToken(memberContext.get(), memberContext.get().getAuthorities());
        }
        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));

    }

    //인증 성공시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);// 시큐리티 컨텍스트에 현재 유저 정보를 저장해둔다
        chain.doFilter(request, response);
    }

    //인증 실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();

        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
