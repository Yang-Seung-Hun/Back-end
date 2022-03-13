package com.hanghae99.boilerplate.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.TestLoginMember;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AjaxLoginProcessingFilterTest {

    @Autowired
    MockMvc mockMvc; //


    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void get요청을login으로보낼떄() throws Exception {
        mockMvc.perform(get("/api/login"))
                .andExpect(status().isBadRequest())
                ;
    }

    @Test
    public void login시변수명불일치param전달() throws Exception{
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString( new TestLoginMember("213","123"))))
                .andDo(print())
                .andExpect(jsonPath("message").exists());
    }
    @Test
    public void login시아무것도안주기()throws Exception{
        mockMvc.perform(post("/api/login"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void login시아이디만존재패스워드없음() throws  Exception{
        SignupReqestDto signupReqestDto = new SignupReqestDto("hojun", "hojun", "111", "444");

        mockMvc.perform(post("/api/signup")
                .content(objectMapper.writeValueAsString(signupReqestDto)));

        LoginRequestDto loginRequestDto= new LoginRequestDto(signupReqestDto.getEmail(),"123");
        mockMvc.perform(post("/api/login")
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}