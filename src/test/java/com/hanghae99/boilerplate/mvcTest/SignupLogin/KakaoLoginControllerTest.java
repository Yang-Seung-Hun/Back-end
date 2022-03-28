//package com.hanghae99.boilerplate.mvcTest.SignupLogin;
//
//import com.hanghae99.boilerplate.memberManager.model.Member;
//import com.hanghae99.boilerplate.security.config.JwtConfig;
//import com.hanghae99.boilerplate.security.model.MemberContext;
//import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
//import com.hanghae99.boilerplate.signupLogin.kakao.common.KakaoUserData;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.io.IOException;
//import java.util.stream.Collectors;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//public class KakaoLoginControllerTest extends Config {
//
//    static TemporaryUser temporaryUser;
//    static KakaoUserData kakaoUserData;
//    static Member member;
//
//
//
//    @BeforeAll
//    static void setParam() {
//        kakaoUserData = new KakaoUserData("access_token", "refresh_token");
//        temporaryUser = new TemporaryUser("wns674@naver.com", "최호준", "이미지1번");
//        member = new Member(temporaryUser);
//    }
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Test
//    @DisplayName("정상적인 code+ 정상적인 toekn 으로 요청 보낼시")
//    public void requestNormal() throws Exception {
//        Mockito.when(kakaoLoginService.getKakaoUserInformation(any(String.class))).thenReturn(temporaryUser);
//        Mockito.when(registerMember.registerKakaoUserToMember(temporaryUser)).thenReturn(
//                new MemberContext(member.getEmail(), member.getRoles().stream().map(role ->
//                        new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()), member.getNickname(), 10L));
//
//
//        doNothing().when(redis).setExpire(any(String.class), any(String.class), any(Integer.class));
//        mockMvc.perform(get("/api/kakao/login")
//                        .param("code", "normal code"))
//                .andExpect(status().isOk())
//                .andExpect(header().exists(JwtConfig.AUTHENTICATION_HEADER_NAME))
//                .andExpect(cookie().exists(JwtConfig.AUTHENTICATION_HEADER_NAME));
//
//    }
//
//    @Test
//    @DisplayName("나쁜 토큰으로 요청")
//    public void requestBad() throws Exception {
//        Mockito.when(kakaoLoginService.getKakaoUserInformation(any())).thenThrow(IOException.class);
//        mockMvc.perform(get("/api/kakao/login")
//                        .param("code", "bad code"))
//                .andExpect(status().is5xxServerError());
//
//    }
//
//
//}
