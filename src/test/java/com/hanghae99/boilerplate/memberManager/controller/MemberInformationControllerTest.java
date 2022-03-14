package com.hanghae99.boilerplate.memberManager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class MemberInformationControllerTest {

    @Autowired
    MockMvc mockMvc; //


    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    MemberRepository memberRepository;



    @Test
    @DisplayName("정상적인 입력으로 회원가입")
    @Transactional
    public void signupTest() throws Exception {
        //Givens
        SignupReqestDto member = new SignupReqestDto("wns", "password", "profile", "image");
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

        MvcResult mvcResult=  mockMvc.perform(post("/api/login")
                .content(objectMapper.writeValueAsString(new LoginRequestDto(member.getEmail(),member.getPassword()))))
                .andExpect(status().isOk()).andReturn();
        String access_token = mvcResult.getResponse().getHeader("Authorization");
        Cookie[] cookies = mvcResult.getResponse().getCookies();

        Cookie cookie = new Cookie("Authorization", cookies[0].getValue());
        mockMvc.perform(get("/auth/users")
                        .cookie(cookie)
                        .header("Authorization", "Bearer " + access_token))
                .andDo(print())
                .andExpect(status().isOk());
        memberRepository.deleteAll();
    }

}