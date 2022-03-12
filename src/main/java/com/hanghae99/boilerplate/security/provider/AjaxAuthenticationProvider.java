package com.hanghae99.boilerplate.security.provider;

import com.hanghae99.boilerplate.model.Member;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AjaxAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String email = authentication.getName();
            String password = (String) authentication.getCredentials();
            UserDetails member = userDetailsService.loadUserByUsername(email);

            if (!passwordEncoder.matches(password, member.getPassword())) {
                log.info("password not matches {} , {} ", member.getPassword(), password);
                log.info("{}", password, member.getPassword());
                throw new BadCredentialsException("패스워드가 일치하지 않습니다");
            }

            MemberContext memberContext = MemberContext.create(member.getUsername(),
                    (List<GrantedAuthority>) member.getAuthorities());
            log.debug("id :{} , role :{} ", memberContext.getUsername(), memberContext.getAuthorities().get(0));
            return new UsernamePasswordAuthenticationToken(memberContext, null, memberContext.getAuthorities());
        }catch (Exception e){
            throw new BadCredentialsException(e.getMessage());
        }
    }


    //처리 가능한 타입인지 CHECK
    @Override //dao ~ form ~~ajax 여러가지 타입중에서 사용 가능한 타입인지
    public boolean supports(Class<?> authentication) {
        //Class 객체가 기본 유형을 나타내는 경우 지정된 Class 매개변수가 정확히 이 Class 객체이면 이 메서드는 true를 반환합니다. 그렇지 않으면 false를 반환합니다.
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
