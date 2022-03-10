package com.hanghae99.boilerplate.exception;

import com.hanghae99.boilerplate.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
public class TemporaryUser{
    private String email;
    private String nickname;
    private String ProfileImageUrl;
}
