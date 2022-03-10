package com.hanghae99.boilerplate.dto.responseDto;

import com.hanghae99.boilerplate.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String email;
    private String nickname;
    private Set<Role> role;
}
