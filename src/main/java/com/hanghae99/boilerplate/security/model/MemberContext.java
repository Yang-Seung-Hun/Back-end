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

    private Long memberId;

    //principal로 catsing 되는 객체
    private final String username;
    private final List<GrantedAuthority> authorities;
    public static MemberContext create(Long memberId, String email,List<GrantedAuthority> authorities){
        if(email==null || email.isBlank()){
            throw new IllegalArgumentException("email is Blank Or Null");
        } else {
            return new MemberContext(memberId, email, authorities);
        }
    }
}
