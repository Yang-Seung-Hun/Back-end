package com.hanghae99.boilerplate;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.model.Member;
import com.hanghae99.boilerplate.model.Role;
import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import com.hanghae99.boilerplate.service.SignupLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SignupLoginTest {


    @Autowired
    MockMvc mockMvc; //
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncode;
    @Autowired
    SignupLoginService signupLoginService;

    @Autowired
    ObjectMapper objectMapper;


    /**
     * @100명 24s
     */

    @Test
    @DisplayName("회원가입100명")
    public void signupTest() throws Exception {
        //Givens

        long startTime = System.currentTimeMillis();

        List<Member> memberList = makeMember(100);
        for (Member member : memberList) {

            mockMvc.perform(post("/api/signup/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(member)))
                    .andExpect(status().isOk());
        }
        long finishTime = System.currentTimeMillis();
        System.out.printf("TOTAL TIME = %s", (finishTime - startTime));
    }

    /**
     * @100명로그인시간:
     * @51.37s
     */
    @Test
    @DisplayName("회원가입한 유저들이 로그인")
    public void signupDisPlay() throws Exception {
        //Givens
        StopWatch stopWatch = new StopWatch();
        List<Member> memberList = makeMember(100);
        stopWatch.start();
        for (Member member : memberList) {
            mockMvc.perform(post("/api/login")
                            .content(objectMapper.writeValueAsString(new LoginRequestDto(member.getEmail(), member.getPassword()))))
                    .andExpect(status().isOk())

                    .andExpect(jsonPath("access_token").exists());
        }
        stopWatch.stop();
        System.out.println("elapsedTime(ms) : " + stopWatch.getTotalTimeMillis());

    }


    private List<Member> makeMember(int count) throws Exception {
        List<Member> memberList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String email = "hojun@aaa" + i;
            String nickname = "hojun" + i;
            String image = "hojun" + i;
            String password = "hojun" + i;
            Member member = new Member(new SignupReqestDto(email, nickname, password, image));
            memberList.add(member);

            mockMvc.perform(post("/api/signup/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(member)))
                    .andExpect(status().isOk());

        }


        return memberList;
    }

}
