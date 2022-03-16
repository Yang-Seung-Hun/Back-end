package com.hanghae99.boilerplate.signupLogin.kakao.common;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class KakaoUserData {
    private String access_token;
    private String refresh_token;

}

