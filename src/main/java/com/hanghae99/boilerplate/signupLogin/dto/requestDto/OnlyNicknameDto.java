package com.hanghae99.boilerplate.signupLogin.dto.requestDto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class OnlyNicknameDto {

    @NotBlank(message = "nickame not blank")
    private String nickname;
}
