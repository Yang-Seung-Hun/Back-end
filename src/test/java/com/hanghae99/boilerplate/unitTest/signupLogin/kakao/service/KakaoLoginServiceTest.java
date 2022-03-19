package com.hanghae99.boilerplate.unitTest.signupLogin.kakao.service;

import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import com.hanghae99.boilerplate.signupLogin.kakao.common.Connection;
import com.hanghae99.boilerplate.signupLogin.kakao.common.KakaoUserData;
import com.hanghae99.boilerplate.signupLogin.kakao.service.KakaoLoginService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.internal.configuration.injection.filter.MockCandidateFilter;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.net.ConnectException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class KakaoLoginServiceTest {

    @Mock
    Connection connection;

    @InjectMocks
    KakaoLoginService kakaoLoginService = new KakaoLoginService();

    @Test
    @DisplayName("정상code에 정상적인 token")
    void kakaoLoginSuccess() throws IOException {
        String code = "123";
        KakaoUserData kakaoUserData = new KakaoUserData("access_token", "refresh_token");
        TemporaryUser temporaryUser = new TemporaryUser("wns@123", "wns", "image");
        Mockito.when(connection.getaccessToken(code)).thenReturn(kakaoUserData);
        Mockito.when(connection.getUserData(any(String.class))).thenReturn(temporaryUser);
        Assertions.assertEquals(kakaoLoginService.getKakaoUserInformation(code), temporaryUser);
    }

    @Test
    @DisplayName("code가 null AuthenticationException")
    void kakaoLoginNullCode() {
        assertThrows(AuthenticationException.class, () -> {
            kakaoLoginService.getKakaoUserInformation(null);
        });


    }

    @Test
    @DisplayName("code가 blank")
    void kakaoLoginBlankCode() {
        assertThrows(AuthenticationException.class, () -> {
            kakaoLoginService.getKakaoUserInformation("   ");

        });
    }




    //IO ->  ConnectionException  -> SocketException
    @Test
    @DisplayName("code가 잘못된경우 request가 IOException ")
    void kakaoLoginBadCode() throws IOException {
        Mockito.when(connection.getaccessToken(any(String.class))).thenThrow(ConnectException.class);
        assertThrows(IOException.class,()->{
            kakaoLoginService.getKakaoUserInformation("bad code!!!!");
        });

    }
    @Test
    @DisplayName(" 잘못된 토큰인경우   IOException ")
    void kakaoBadConnection() throws IOException {
        KakaoUserData kakaoUserData = new KakaoUserData("13", "13");

        Mockito.when(connection.getaccessToken(any(String.class))).thenReturn(kakaoUserData);
        Mockito.when(connection.getUserData(any(String.class))).thenThrow(ConnectException.class);
        assertThrows(IOException.class, () -> {
            kakaoLoginService.getKakaoUserInformation("13");
        });
    }
}