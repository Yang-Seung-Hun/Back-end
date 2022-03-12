package com.hanghae99.boilerplate;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.boilerplate.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.model.Role;
import com.hanghae99.boilerplate.repository.MemberRepository;
import com.hanghae99.boilerplate.repository.RefreshTokenRepository;
import com.hanghae99.boilerplate.security.jwt.TokenFactory;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SignupLoginTest {


    @Autowired
    MockMvc mockMvc; //


    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TokenFactory tokenFactory;
    @Autowired
    RefreshTokenRepository tokenRepository;

    /**
     * @100명 24s
     */

    @Test
    @DisplayName("정상적인 입력으로 회원가입")
    @Transactional
    public void signupTest() throws Exception {
        //Givens
        SignupReqestDto member = new SignupReqestDto("", "password", "profile", "image");

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        memberRepository.deleteAll();
    }


    @Test
    @Transactional

    public void 이름누락회원가입() throws Exception {
        //Givens

        SignupReqestDto signupReqestDto = new SignupReqestDto("", "jun", "111", "444");
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupReqestDto)))
                .andExpect(status().isBadRequest());
        memberRepository.deleteAll();
    }

    @Test
    @Transactional

    public void 비밀번호누락회원가입() throws Exception {

        SignupReqestDto signupReqestDto = new SignupReqestDto("hojun", "", "111", "444");
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupReqestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional

    public void 이미지누락회원가입() throws Exception {

        SignupReqestDto signupReqestDto = new SignupReqestDto("hojun", "aaa", "", "444");
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupReqestDto)))
                .andExpect(status().isBadRequest());
        memberRepository.deleteAll();
    }

    @Test
    @Transactional //update로 바꿔서 테스트 시도
    public void 이미있는이메일로회원가입() throws Exception {
        //Given
        SignupReqestDto member = signup();
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());
        //when & then
        SignupReqestDto member2 = member;
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member2)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value(member2.getEmail() + " already" + "Exist!"));

        memberRepository.deleteAll();
    }

    @Test
    @Transactional
    public void 정상적인로그인요청() throws Exception {
        //Given
        SignupReqestDto member = signup();
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequestDto(member.getEmail(),member.getPassword()))))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(jsonPath("nickname").exists())
                .andExpect(jsonPath("email").exists());
        memberRepository.deleteAll();

    }

    @Test
    @Transactional
    public void 비밀번호누락로그인요청() throws Exception {
        SignupReqestDto member = signup();
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDto(member.getEmail(),""))))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").exists());
        memberRepository.deleteAll();

    }

    @Test
    @Transactional
    public void 아이디누락로그인요청() throws Exception {
        SignupReqestDto member = signup();
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequestDto("",member.getPassword()))))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message").exists());
        memberRepository.deleteAll();
    }

    @Test
    @Transactional
    public void 그냥auth경로에접근() throws Exception {
        mockMvc.perform(post("/auth"))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @Transactional
    public void Authentitcation헤더에잘못된값넣고접근()throws Exception{
        mockMvc.perform(post("/auth")
                .header("Authorization","123"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @Transactional
    public void 발급받은토큰으로auth경로에접근() throws Exception {
        SignupReqestDto member = signup();
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());


        String token = makeAccessToken(member.getEmail());
        mockMvc.perform(post("/auth")
                .header("Authorization","Bearer "+token))
                .andExpect(status().isNotFound());
        memberRepository.deleteAll();


    }
    @Test
    @Transactional
    public void 토큰만료쿠키없음uth경로접근() throws Exception{

        SignupReqestDto member = signup();
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

        String expiredToken = makeExpiredToken(member.getEmail());
        System.out.println(expiredToken);
        mockMvc.perform(post("/auth")
                .header("Authorization","Bearer "+expiredToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        memberRepository.deleteAll();
        tokenRepository.deleteAll();

    }

    @Test
    @Transactional
    public void 토큰만료쿠키있음auth경로접근() throws Exception {

        SignupReqestDto member = signup();
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk());

        String accessToken = makeExpiredToken(member.getEmail());
        System.out.println(accessToken);
        String refreshToken = makeRefreshToken(member.getEmail());


        Cookie cookie = new Cookie("Authorization",refreshToken);
        mockMvc.perform(post("/auth")
                        .cookie(cookie)
                        .header("Authorization","Bearer "+ accessToken))
                .andDo(print())
                .andExpect(status().isNotFound());
        memberRepository.deleteAll();
        tokenRepository.deleteAll();
    }



    private String  makeAccessToken(String email){

        Set<Role>roles= new HashSet<>();
        roles.add(Role.USER);
        MemberContext memberContext = new MemberContext(email,roles.stream().map(role->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()));

        JwtToken accessToken = tokenFactory.createAccessToken(memberContext);
        return accessToken.getToken();
    }


    private String makeExpiredToken(String email){

        Set<Role>roles= new HashSet<>();
        roles.add(Role.USER);
        MemberContext memberContext = new MemberContext(email,roles.stream().map(role->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()));

        JwtToken accessToken = tokenFactory.createTestTokenExpired(memberContext);
        return accessToken.getToken();
    }
    private String makeRefreshToken(String email){
        Set<Role>roles= new HashSet<>();
        roles.add(Role.USER);
        MemberContext memberContext = new MemberContext(email,roles.stream().map(role->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()));

        JwtToken accessToken = tokenFactory.createRefreshToken(memberContext);
        return accessToken.getToken();
    }



    private SignupReqestDto signup() throws Exception {
        String email = "hojun@aaa";
        String nickname = "hojun";
        String profileImageUrl = "hojun";
        String password = "hojun";
        SignupReqestDto member = new SignupReqestDto(email, nickname, password, profileImageUrl);
        return member;
    }
}


