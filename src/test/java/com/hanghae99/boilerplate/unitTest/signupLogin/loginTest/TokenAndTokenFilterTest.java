package com.hanghae99.boilerplate.unitTest.signupLogin.loginTest;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.security.RefreshTokenEndPoint;
import com.hanghae99.boilerplate.security.SkipPathRequestMatcher;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.filter.JwtTokenAuthenticationProcessingFilter;
import com.hanghae99.boilerplate.security.jwt.AccessToken;
import com.hanghae99.boilerplate.security.jwt.Scopes;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenExtractor;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.JwtSignatureValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.hanghae99.boilerplate.security.config.SecurityConfig.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration
public class TokenAndTokenFilterTest {

    @Mock
    private AuthenticationFailureHandler failureHandler;
    @Spy
    private TokenExtractor tokenExtractor;
    @Spy
    JwtConfig jwtConfig;
    @InjectMocks
    TokenFactory tokenFactory = new TokenFactory();


    @Mock
    private RefreshTokenEndPoint refreshTokenEndPoint;
    List<String> pathsToSkip = Arrays.asList(REFRESH_TOKEN_URL, SIGNUP_URL, AUTHENTICATION_URL, SWAGGER, SWAGGER_DOCS);
    SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(pathsToSkip, AUTH_ROOT_URL);

    @InjectMocks
    JwtTokenAuthenticationProcessingFilter jwtFilter = new JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher, refreshTokenEndPoint);

    TemporaryUser temporaryUser = new TemporaryUser("wns674@naver.com", "ghwns", "123");


    @Test
    @DisplayName("헤더가 없는경우 AuthenticationServiceException")
    void EmptyToken() {
        //Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
//        request.addHeader(AUTHENTICATION_HEADER_NAME,);

        assertThrows(AuthenticationServiceException.class,
                () -> {
                    jwtFilter.attemptAuthentication(request, response);
                });

    }

    @Test
    @DisplayName("헤더가 있지만 blank인경우  AuthenticationServiceException ")
    void noValueHeader() {
        //Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader(AUTHENTICATION_HEADER_NAME, " ");
        assertThrows(AuthenticationServiceException.class,
                () -> {
                    jwtFilter.attemptAuthentication(request, response);
                });

    }

    @Test
    @DisplayName("새로 만든 토큰이 유효한지 확인")
    void checkValidToken() {
        String token = makeToken();

        TokenVerifier tokenVerifier = new TokenVerifier();
        tokenVerifier.validateToken(token, jwtConfig.getTokenSigningKey());

    }

    @Test
    @DisplayName("만료되었고 서명도 잘못된 경우 서명에러를 먼저 반환하는가? ")
    void expiredToken() {
        String token = "13.3.2";
        try {
            TokenVerifier tokenVerifier = new TokenVerifier();
            tokenVerifier.validateToken(token, jwtConfig.getTokenSigningKey());
        } catch (ExpiredJwtException e) {
            fail();
        } catch (JwtException e) {
            return;
        }
        fail();
    }

    @Test
    @DisplayName("만료된 토큰을 ExpiredJwtException로 반환하는가")
    void badTokenErrorFirst() {
        String token = makeExpiredToken();

            TokenVerifier tokenVerifier = new TokenVerifier();
          assertThrows(ExpiredJwtException.class,()->{
              tokenVerifier.validateToken(token, jwtConfig.getTokenSigningKey());
          }) ;
    }

    @Test
    @DisplayName("다른사람이 만든 만료된 토큰 expired를 반환해선 안된다")
    void badSign() {
        String token = makeBadSignToken();
        try {
            TokenVerifier tokenVerifier = new TokenVerifier();
            tokenVerifier.validateToken(token, jwtConfig.getTokenSigningKey());
        } catch (SignatureException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            return;
        } catch (ExpiredJwtException E) {
        }
        fail();
    }


    String makeToken() {
        String token = tokenFactory.createAccessToken(new MemberContext(new Member(temporaryUser))).getToken();
        return token;

    }

    String makeExpiredToken() {


        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().setSubject("hojun");
        claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(String.valueOf(1))// Issuer : member ID
                .setAudience("jun")                //Audience :member nickname
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .minusDays(jwtConfig.getRefreshTokenExpTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getTokenSigningKey())
                .compact();

        return token;
    }

    String makeBadSignToken() {

        LocalDateTime currentTime = LocalDateTime.now();

        Claims claims = Jwts.claims().setSubject("hojun");
        claims.put("scopes", Arrays.asList(Scopes.REFRESH_TOKEN.authority()));

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(String.valueOf(1))// Issuer : member ID
                .setAudience("jun")                //Audience :member nickname
                .setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(currentTime
                        .plusDays(jwtConfig.getRefreshTokenExpTime())
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, "123".getBytes(StandardCharsets.UTF_8))
                .compact();

        return token;
    }
}



