package com.hanghae99.boilerplate.security.model;

import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberContext {
    //principal로 catsing 되는 객체
    private  String username;
    private  List<GrantedAuthority> authorities;
    private  String nickname;
    private  Long memberId;

    public MemberContext(Member member) {
        this.username=member.getEmail();
        this.nickname=member.getNickname();
        this.authorities = member.getRoles().stream().map( role ->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
    this.memberId=member.getId();
    }

    public static MemberContext create(String email, List<GrantedAuthority> authorities,String nickname,Long id) {
        return new MemberContext(email, authorities,nickname,id);
    }
}
