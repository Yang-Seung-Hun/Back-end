//package com.hanghae99.boilerplate.board.controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hanghae99.boilerplate.board.dto.BoardRequestDto;
//import com.hanghae99.boilerplate.security.config.JwtConfig;
//import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
//import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.DisabledIf;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//class BoardControllerTest {
////{ "email":"1", "password":"111"
////,"profileImageUrl":"hhh",
////"nickname":"123"
////}
//    @Autowired
//    MockMvc mockMvc;
//    @Autowired
//    ObjectMapper objectMapper;
//    @Test
//    @DisplayName("토큰있음")
//    //보드x 실패
//    public void getBoard() throws Exception {
//
//        LoginRequestDto loginRequestDto = new LoginRequestDto("1","111");
//        MvcResult mvcResult= mockMvc.perform(post("/api/login")
//                .content(objectMapper.writeValueAsString(loginRequestDto)))
//                .andExpect(status().isOk())
//                .andReturn();
//        String token = mvcResult.getResponse().getHeader(JwtConfig.AUTHENTICATION_HEADER_NAME);
//
//        mockMvc.perform(get("/auth/api/board/1")
//                .header(JwtConfig.AUTHENTICATION_HEADER_NAME,JwtConfig.TOKEN_TYPE+ token))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//    }
//
//    @Test
//    @DisplayName("1번 증명 테스트")
//    public void auth경로에토큰가지고접근시200() throws Exception {
//
//        LoginRequestDto loginRequestDto = new LoginRequestDto("1","111");
//        MvcResult mvcResult= mockMvc.perform(post("/api/login")
//                        .content(objectMapper.writeValueAsString(loginRequestDto)))
//                .andExpect(status().isOk())
//                .andReturn();
//        String token = mvcResult.getResponse().getHeader(JwtConfig.AUTHENTICATION_HEADER_NAME);
//
//        mockMvc.perform(get("/auth")
//                        .header(JwtConfig.AUTHENTICATION_HEADER_NAME,JwtConfig.TOKEN_TYPE+ token))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//
//    }
//
//    @Test
//    @DisplayName("MemberContext에 email,nickname,id 가 있는지 확인")
//    public void 권한있는접근() throws Exception {
//
//        SignupReqestDto member = new SignupReqestDto("qwerqwer", "password", "profile", "image");
//
////        mockMvc.perform(post("/api/signup")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(member)))
////                .andDo(print())
////                .andExpect(status().isOk());
//
//
//        LoginRequestDto loginRequestDto = new LoginRequestDto(member.getEmail(),member.getPassword());
//        MvcResult mvcResult= mockMvc.perform(post("/api/login")
//                        .content(objectMapper.writeValueAsString(loginRequestDto)))
//                .andExpect(status().isOk())
//                .andReturn();
//        String token = mvcResult.getResponse().getHeader(JwtConfig.AUTHENTICATION_HEADER_NAME);
//
//        mockMvc.perform(get("/auth/test")
//                        .header(JwtConfig.AUTHENTICATION_HEADER_NAME,JwtConfig.TOKEN_TYPE+ token))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("username").value(loginRequestDto.getEmail()))
//                .andExpect(jsonPath("nickname").value(member.getNickname()));
//
//    }
//
//    @Test
//    @DisplayName("토큰이 ㅇㅏ예 없음")
//    public void getBoardNoToken() throws Exception {
//
//        mockMvc.perform(get("/auth/api/board/10"))
//                .andDo(print())
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @DisplayName("잘못된토큰")
//    public void getBoardBadToken() throws Exception {
//
//        mockMvc.perform(post("/auth/api/board/10")
//                        .header(JwtConfig.AUTHENTICATION_HEADER_NAME,"123"))
//                .andDo(print())
//                .andExpect(status().isUnauthorized());
//
//    }
//}