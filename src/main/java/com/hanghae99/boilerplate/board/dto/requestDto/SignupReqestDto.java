package com.hanghae99.boilerplate.board.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor//테스트 전용
public class SignupReqestDto {

    @NotBlank(message = "email을 입력하세요")
    private String email;

    @NotBlank(message = "nickname을 입력하세요")
    private String nickname;

    @NotBlank(message = "password를 입력하세요")
    private String password;

    @NotBlank(message="image를 등록하세요")
    private String profileImageUrl;


}