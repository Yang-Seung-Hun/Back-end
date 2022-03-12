package com.hanghae99.boilerplate.dto.responseDto;

import com.hanghae99.boilerplate.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Setter
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private Long id;

    private String email;
    private String nickname;
    private Set<Role> role;
}
