package com.hanghae99.boilerplate.mvcTest.SignupLogin;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import com.hanghae99.boilerplate.security.service.UserDetailsImpl;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SelfSignupLoginTest extends Config {

    //setting
    static SignupReqestDto normalSignupReqestDto;
    static LoginRequestDto normalLoginRequestDto;

    static SignupReqestDto badSignupReqestDto;
    static LoginRequestDto badLoginRequestDto;
    static Member member;

    @BeforeAll
    static void makeMember() {
        normalSignupReqestDto = new SignupReqestDto("wns674@naver.com", "?????????", "1234", "?????????1???");
        normalLoginRequestDto = new LoginRequestDto(normalSignupReqestDto.getEmail(), normalSignupReqestDto.getPassword());
        member = new Member(normalSignupReqestDto);
        badSignupReqestDto = new SignupReqestDto("wns674@naver.com", "?????????", null, "?????????1???");
        badLoginRequestDto = new LoginRequestDto("?????????", normalSignupReqestDto.getPassword());
    }



    @Autowired
    MockMvc mockMvc;


    @Test
    @DisplayName("???????????? ?????? signRequest??? ??????")
    void ??????????????????200() throws Exception {
        Mockito.when(signupLoginService.signupRequest(any())).thenReturn(new MemberContext(member));
        MockHttpServletResponse result= mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalSignupReqestDto)))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
                .andExpect(cookie().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
                .andReturn().getResponse();
        redis.removeData(result.getCookie(JwtConfig.AUTHENTICATION_HEADER_NAME).getValue());




    }


    @Test
    @DisplayName("???????????? ??????signRequst??? ?????????")
    void ??????????????????400() throws Exception {
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badSignupReqestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").exists());
    }


    @Test
    @DisplayName("????????? ?????? normalLoginRequestDto??? ")
    void ???????????????200() throws Exception {

        Mockito.when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);
        Mockito.when(userDetails.loadUserByUsername(any(String.class))).thenReturn(new UserDetailsImpl(member.getEmail(), member.getPassword(), member.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()), member.getNickname(), 123L));


        MockHttpServletResponse response= mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(normalLoginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("email").exists())
                .andReturn().getResponse();
        redis.removeData(response.getCookie(JwtConfig.AUTHENTICATION_HEADER_NAME).getValue());

    }

    @Test
    @DisplayName("????????? ?????? ???????????? ????????? ")
    void ???????????????401() throws Exception {
        Mockito.when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);
        Mockito.when(userDetails.loadUserByUsername(any(String.class))).thenReturn(new UserDetailsImpl(member.getEmail(), member.getPassword(), member.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()), member.getNickname(), 123L));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLoginRequestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").exists());
    }

    @Test
    @DisplayName("???????????? ????????? ?????? ?????? UsernameNotFoundException???????????? 401??? ????????? ")
        //????????? ??????????????? entryPoint?????? ????????? ???????????? ????????? ????????? message ??? null??? ??????
    void ?????????????????????????????????x() throws Exception {
        Mockito.when(userDetails.loadUserByUsername(any(String.class))).thenThrow(UsernameNotFoundException.class);
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLoginRequestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("???????????? ??????????????? true??? ??????")
    void ???????????????????????????true() throws Exception {
        Mockito.when(signupLoginService.DuplicatesEmail(any())).thenReturn(true);
        String email = "{ \"email\" : \"email@namver.com\"  }";
        mockMvc.perform(post("/api/user/check/Email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("true"));

    }

    @Test
    @DisplayName("????????? ?????? x true??? ??????")
    void ???????????????x() throws Exception {
        Mockito.when(signupLoginService.DuplicatesEmail(any())).thenReturn(false);
        String email = "{ \"email\" : \"email@namver.com\"  }";
        mockMvc.perform(post("/api/user/check/Email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(email)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("false"));
    }




}
