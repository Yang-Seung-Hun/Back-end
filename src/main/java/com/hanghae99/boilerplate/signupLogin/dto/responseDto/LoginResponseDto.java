package com.hanghae99.boilerplate.signupLogin.dto.responseDto;

import com.hanghae99.boilerplate.memberManager.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;


@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String email;
    private String nickname;
    private Set<Role> role;
}
