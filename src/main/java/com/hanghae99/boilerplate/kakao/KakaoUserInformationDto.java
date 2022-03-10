package com.hanghae99.boilerplate.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * @code를 주고 받아온 데이터
 */

@Getter
@Setter
@NoArgsConstructor
public class KakaoUserInformationDto {

    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String scope;
    private String refresh_token_expires_in;



}
