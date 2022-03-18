//package com.hanghae99.boilerplate.signupLogin.Controller;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
//import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
//import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.context.web.WebAppConfiguration;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@ExtendWith(SpringExtension.class)
//class SignupLoginControllerTest {
//
//
//    @Autowired
//    MockMvc mockMvc;
//    @Autowired
//    ObjectMapper objectMapper;
//    @Autowired
//    MemberRepository memberRepository;
//
//    SignupReqestDto signupReqestDto = new SignupReqestDto("wns674@naver.com","최호준","1234","이미지1번");
//    LoginRequestDto loginRequestDto= new LoginRequestDto(signupReqestDto.getEmail(), signupReqestDto.getPassword());
//
//    @Test
//    @DisplayName("모든게 정상 회원가입")
//    void singupBadArg() throws Exception {
//        mockMvc.perform(post("/api/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(signupReqestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("message").value("signup success"));
//        memberRepository.deleteAll();
//
//    }
//}