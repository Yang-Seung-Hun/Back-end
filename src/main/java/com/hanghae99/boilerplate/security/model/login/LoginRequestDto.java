package com.hanghae99.boilerplate.security.model.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    private String email;
    private String password;
}
