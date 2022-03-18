package com.hanghae99.boilerplate.unitTest.signupLogin.kakao.service;

import com.hanghae99.boilerplate.signupLogin.kakao.common.Connection;
import com.hanghae99.boilerplate.signupLogin.kakao.common.KakaoUserData;
import com.hanghae99.boilerplate.signupLogin.kakao.service.KakaoLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.rmi.ConnectException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
public class KakaoLoginServiceTestNotMock {

    @Spy
    Connection connection ;

    @InjectMocks
    KakaoLoginService kakaoLoginService = new KakaoLoginService();

    //IO ->  ConnectionException  -> SocketException
    @Test
    @DisplayName("code가 잘못된경우 request가 IOException ")
    void kakaoLoginBadCode(){
        assertThrows(IOException.class,()->{
            kakaoLoginService.getKakaoUserInformation("bad code!!!!");
        });

    }
    @Test
    @DisplayName(" 잘못된 토큰인경우   ConnectException ")
    void kakaoBadConnection() throws IOException {
        KakaoUserData kakaoUserData= new KakaoUserData("13","13");

        Mockito.when(connection.getaccessToken(any(String.class))).thenReturn(kakaoUserData);

        assertThrows(ConnectException.class ,()->{

        kakaoLoginService.getKakaoUserInformation("13");
        });
    }
}
