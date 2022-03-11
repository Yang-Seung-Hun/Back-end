package com.hanghae99.boilerplate.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.security.model.RefreshTokenDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Slf4j
@Component
public class AjaxAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private MemberRepository memberRepository;

    private RefreshTokenRepository refreshTokenRepository;

    private TokenFactory tokenFactory;

    private ObjectMapper objectMapper;
    public AjaxAuthenticationSuccessHandler(TokenFactory tokenFactory, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository,ObjectMapper objectMapper) {
        this.memberRepository = memberRepository;
        this.tokenFactory = tokenFactory;
        this.refreshTokenRepository = refreshTokenRepository;
        this.objectMapper= objectMapper;
    }


    //인증 성공시 호출된다  //name이 null일 일이 없다
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        try {
            MemberContext memberContext = (MemberContext) authentication.getPrincipal();

            JwtToken accessToken = tokenFactory.createAccessToken(memberContext);
            JwtToken refreshToken = tokenFactory.createRefreshToken(memberContext);


            Map<String, String> tokenMap = new HashMap<String, String>();
            tokenMap.put("access_token", accessToken.getToken());


            Cookie cookie = new Cookie("Authentitcation", refreshToken.getToken());
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60 * 24);
            cookie.setPath("/");
            response.addCookie(cookie);

            refreshTokenRepository.deleteToken(memberContext.getUsername());
            refreshTokenRepository.save(new RefreshTokenDB(memberContext.getUsername(),
                    refreshToken.getToken()));

            Optional<String> nickname = memberRepository.getNickname(memberContext.getUsername());
            nickname.ifPresent(s -> tokenMap.put("nickname", s));


            objectMapper.writeValue(response.getWriter(), tokenMap);
        }
        catch (IllegalArgumentException e){
            log.debug("{}",e.getMessage());
        }catch (NullPointerException e){
            log.debug(e.getMessage());
        }
    }


    //저장되었을 수 있는 임시 인증 관련 데이터 제거
//     인증 프로세스 중 세션에서..
/*    @Override
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session == null) {
            return;
        }
        //필요 없는 데이터들을 security context
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }*/
}
