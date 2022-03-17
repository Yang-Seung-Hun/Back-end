package com.hanghae99.boilerplate.memberManager.mail;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class FindPasswordDto {

    @NotBlank(message = "email not blank")
    @Email(message ="check email")
    private String email;
}
