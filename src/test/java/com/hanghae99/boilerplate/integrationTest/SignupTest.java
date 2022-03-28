package com.hanghae99.boilerplate.integrationTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import org.hibernate.AssertionFailure;
import org.junit.BeforeClass;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SignupTest extends Config {

    @Autowired
    MemberRepository memberRepository;

    SignupReqestDto normalSignupReqestDto = new SignupReqestDto("wns67431231@naver.com", "최호준", "1234", "이미지1번");
    SignupReqestDto badSignupReqestDto = new SignupReqestDto("wns67431231@naver.com", "최호준", "", "이미지1번");
    String email = "{ \"email\" : "+ "\""+ normalSignupReqestDto.getEmail()+"\" }";



    @Test
    @Order(-1)
    @Transactional
    void before(){
        memberRepository.deleteByEmail(normalSignupReqestDto.getEmail());
    }

    @Transactional
    void after(){
        memberRepository.deleteByEmail(normalSignupReqestDto.getEmail());
    }



    @Test
    @Transactional
    void 회원가입성공() throws Exception {
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalSignupReqestDto)))
                .andExpect(status().isOk());
        Assertions.assertEquals(memberRepository.findByEmail(normalSignupReqestDto.getEmail()).get().getNickname(), normalSignupReqestDto.getNickname());        memberRepository.deleteAll();
        after();
    }




    @Test
    @DisplayName("회원가입시 토큰이 온다")
    @Transactional
    void 회원가입요청시토큰() throws Exception {

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalSignupReqestDto)))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
                .andExpect(cookie().exists(JwtConfig.AUTHENTICATION_HEADER_NAME));
        after();

    }
    @Test
    void signRequesetDto가잘못된경우회원가입() throws Exception {
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badSignupReqestDto)))
                .andExpect(status().isBadRequest());
        Assertions.assertTrue(memberRepository.findByEmail(badSignupReqestDto.getEmail()).isEmpty());
    }

    @Test
    @Transactional
    void email이중복된경우회원가입() throws Exception {
        //given
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalSignupReqestDto)))
                .andExpect(status().isOk());

        //when then

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalSignupReqestDto)))
                .andExpect(status().isBadRequest());
        //AssertionFailure로 트랜잭션이 아예 일어나지 않는다,
        Assertions.assertThrows(AssertionFailure.class ,()->{
            after();
        });
    }

    @Test
    @Transactional
    void 이메일중복체크true() throws Exception {

        //given
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalSignupReqestDto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/user/check/Email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("true"));
        after();
    }

    @Test
    void 이메일중복체크false() throws Exception {

        mockMvc.perform(get("/api/user/check/Email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("false"));
        after();

    }


}
