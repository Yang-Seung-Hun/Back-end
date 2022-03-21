package com.hanghae99.boilerplate.integrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.RefreshTokenRedis;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import io.jsonwebtoken.Jwts;
import org.elasticsearch.monitor.os.OsStats;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthTest extends Config {
    SignupReqestDto signupReqestDto = new SignupReqestDto("wns@naver.com", "최호준", "1234", "이미지1번");

    Member member;
    String password = signupReqestDto.getPassword();
    String access_token;
    String refresh_token;

    @Autowired
    RefreshTokenRedis redis;

    @BeforeEach
    @Transactional
    void login() throws Exception {
        signupReqestDto.setPassword(passwordEncoder.encode(signupReqestDto.getPassword()));
        member = memberRepository.save(new Member(signupReqestDto));
        MockHttpServletResponse response = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDto(signupReqestDto.getEmail(), password))))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
                .andExpect(cookie().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
                .andReturn().getResponse();
        access_token = response.getHeader(JwtConfig.AUTHENTICATION_HEADER_NAME);
        refresh_token = response.getCookie(JwtConfig.AUTHENTICATION_HEADER_NAME).getValue();
    }

    @AfterEach
    void removeMember() {
        memberRepository.delete(member);
        redis.removeData(refresh_token);
    }

    @Test
    void 로그인후얻은토큰검증() throws Exception {

        Assertions.assertDoesNotThrow(() -> vaildToken(access_token));
        Assertions.assertDoesNotThrow(() -> vaildToken(refresh_token));

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtConfig.AUTHENTICATION_HEADER_NAME,JwtConfig.TOKEN_TYPE+ access_token))
                .andExpect(status().isNotFound());
    }

    @Test
    void 토큰만료시리프레시토큰확인후새로운토큰발급() throws Exception {

        Assertions.assertDoesNotThrow(() -> tokenVerifier.validateToken(access_token, JwtConfig.tokenSigningKey));
        Cookie cookie = new Cookie(JwtConfig.AUTHENTICATION_HEADER_NAME,   refresh_token);


        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtConfig.AUTHENTICATION_HEADER_NAME, JwtConfig.TOKEN_TYPE + token())
                        .cookie(cookie))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(JwtConfig.AUTHENTICATION_HEADER_NAME));
    }

    @Test
    void 리프레시토큰엑세스토큰모두만료시접근불가능()throws Exception{
        Cookie cookie = new Cookie(JwtConfig.AUTHENTICATION_HEADER_NAME,   token());

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(JwtConfig.AUTHENTICATION_HEADER_NAME, JwtConfig.TOKEN_TYPE + token())
                        .cookie(cookie))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void 로그아웃시레디스에서지워지는가() throws Exception {
        Cookie cookie = new Cookie(JwtConfig.AUTHENTICATION_HEADER_NAME,refresh_token);
        mockMvc.perform(post("/api/logout")
                .cookie(cookie))
                .andExpect(status().isOk());
        Assertions.assertNull(redis.getData(refresh_token));

    }


    public String token() {
        Member member = memberRepository.findByEmail(signupReqestDto.getEmail()).get();
        MemberContext memberContext = new MemberContext(member);
        return tokenFactory.createToken(memberContext, 0).getToken();
    }

    public void vaildToken(String token) throws Exception {
        try {
            Jwts.parser().setSigningKey(JwtConfig.tokenSigningKey).parseClaimsJws(token);
        }catch (Exception e){
            throw new Exception();
        }
    }


}
