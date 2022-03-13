package com.hanghae99.boilerplate.redisTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.config.Redis;
import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginRedisTest {
    @Autowired
    Redis redis;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;

    private final String TOKEN = "Authorization";
    @Test
    public void 로그인후레디스에들어갔는지확인() throws Exception {
        SignupReqestDto member = new SignupReqestDto("hojun", "password", "profile", "image");

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isOk());
       MvcResult mvcResult=  mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequestDto(member.getEmail(), member.getPassword()))))
                .andDo(print())
               .andExpect(status().isOk())
               .andExpect(header().exists(TOKEN))
                .andExpect(cookie().exists(TOKEN))
               .andReturn();
       String refreshToken=  mvcResult.getResponse().getCookie(TOKEN).getValue();
       Assertions.assertThat(redis.getData(refreshToken)).isEqualTo(member.getEmail());
        memberRepository.deleteAll();

    }

    @Test
    public void 로그아웃후레디스에서지워졌는지확인() throws Exception{
        SignupReqestDto member = new SignupReqestDto("hojun", "password", "profile", "image");

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isOk());
        MvcResult mvcResult=  mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDto(member.getEmail(), member.getPassword()))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists(TOKEN))
                .andExpect(cookie().exists(TOKEN))
                .andReturn();
        String refreshToken=  mvcResult.getResponse().getCookie(TOKEN).getValue();
        Assertions.assertThat(redis.getData(refreshToken)).isEqualTo(member.getEmail());

        Cookie cookie =new Cookie(TOKEN,refreshToken);
        mockMvc.perform(post("/api/logout")
                .cookie(cookie))
                .andDo(print())
                .andExpect(cookie().doesNotExist(TOKEN));


        Assertions.assertThat(redis.getData(refreshToken)).isNull();
        memberRepository.deleteAll();


    }
    


}
