package com.hanghae99.boilerplate.board.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long memberId;
    public Long getMemberId(){
        return this.memberId;
    }


    private String username;
    @JsonIgnore
    private String password;

    //유저가 가진 권한들
    private Collection<? extends  GrantedAuthority> authorities;



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
