package com.hanghae99.boilerplate.integrationTest;

import com.auth0.jwt.JWTVerifier;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoginTest extends Config {


    static SignupReqestDto signupReqestDto = new SignupReqestDto("wns@naver.com", "최호준", "1234", "이미지1번");


    @Test
    @Order(-1)
    @Transactional
    void before(){
        memberRepository.deleteByEmail(signupReqestDto.getEmail());
    }

    @Order(-1)
    @Test
    void saveMember() throws Exception {
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Member(signupReqestDto))))
                .andExpect(status().isOk());
    }


    @Test
    void 성공하는로그인테스트() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto(signupReqestDto.getEmail(), signupReqestDto.getPassword());
        MockHttpServletResponse response = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
                .andExpect(cookie().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
                .andReturn().getResponse();
        Assertions.assertDoesNotThrow(() -> tokenVerifier.validateToken(response.getHeader(JwtConfig.AUTHENTICATION_HEADER_NAME), JwtConfig.tokenSigningKey));
        Assertions.assertDoesNotThrow(() -> tokenVerifier.validateToken(response.getCookie(JwtConfig.AUTHENTICATION_HEADER_NAME).getValue(), JwtConfig.tokenSigningKey));

        redis.removeData(response.getCookie(JwtConfig.AUTHENTICATION_HEADER_NAME).getValue());
    }

    @Test
    void 비밀번호를잘못입력() throws Exception{
        LoginRequestDto loginRequestDto = new LoginRequestDto(signupReqestDto.getEmail(),signupReqestDto.getPassword()+123);
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void 일치하는아이디가없음() throws  Exception{
        LoginRequestDto loginRequestDto = new LoginRequestDto(signupReqestDto.getEmail()+"123",signupReqestDto.getPassword());
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void 아이디비번둘중하나가blank인경우() throws Exception{
        LoginRequestDto loginRequestDto = new LoginRequestDto("",signupReqestDto.getPassword());
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isUnauthorized());

    }

    @AfterClass
    void delAll(){
        memberRepository.deleteAll();
    }


}
