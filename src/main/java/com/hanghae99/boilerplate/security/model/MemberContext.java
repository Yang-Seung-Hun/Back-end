package com.hanghae99.boilerplate.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberContext {
    //principal로 catsing 되는 객체
    private final String username;
    private final List<GrantedAuthority> authorities;

    public static MemberContext create(String email, List<GrantedAuthority> authorities) {

        return new MemberContext(email, authorities);
    }
}
