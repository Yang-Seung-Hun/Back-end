package com.hanghae99.boilerplate.unitTest.signupLogin.kakao.service;

import com.hanghae99.boilerplate.signupLogin.kakao.common.Connection;
import com.hanghae99.boilerplate.signupLogin.kakao.common.KakaoUserData;
import com.hanghae99.boilerplate.signupLogin.kakao.service.KakaoLoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;

import javax.inject.Inject;
import javax.security.sasl.AuthenticationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
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
        try {
            kakaoLoginService.getKakaoUserInformation("bad code!!!!");
            fail();
        } catch (IOException e) {
            return;
        }
        fail();
    }
    @Test
    @DisplayName(" 잘못된 토큰인경우   IOException ")
    void kakaoBadConnection()  {
        KakaoUserData kakaoUserData= new KakaoUserData("13","13");
        try{
        Mockito.when(connection.getaccessToken(any(String.class))).thenReturn(kakaoUserData);

        kakaoLoginService.getKakaoUserInformation("13");
        fail();
        }catch (IOException e){
            return ;
        }
        fail();

    }
}
