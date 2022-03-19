package com.hanghae99.boilerplate.unitTest.signupLogin.loginTest;


import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.security.model.login.LoginRequestDto;
import com.hanghae99.boilerplate.security.provider.AjaxAuthenticationProvider;
import com.hanghae99.boilerplate.security.service.UserDetailsImpl;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration// 시큐리티 테스트
public class LoginFilterTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserDetailsService userDetailsService;

    @InjectMocks
    AjaxAuthenticationProvider ajaxAuthenticationProvider = new AjaxAuthenticationProvider();




    TemporaryUser temporaryUser = new TemporaryUser("wns674@naver.com", "ghwns", "123");
    Member member = new Member(temporaryUser);
    MemberContext memberContext = new MemberContext(member);
    LoginRequestDto loginRequestDto = new LoginRequestDto(member.getEmail(), member.getPassword());
    Authentication token = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

    @Test
    @DisplayName("정상적인 로그인 요청 패스워드 일치")
    void loginRequest() {
        //given
        Mockito.when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(new UserDetailsImpl(member.getEmail(), member.getPassword(), member.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()), member.getNickname(), 10L));
        Mockito.when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(true);

//when
        Authentication newToken = ajaxAuthenticationProvider.authenticate(token);
        //then
        Assertions.assertEquals(((MemberContext) newToken.getPrincipal()).getUsername(), member.getEmail());
    }

    @Test
    @DisplayName("일치하는 유저가 없음")
    void memberNotExist() {

        Mockito.when(userDetailsService.loadUserByUsername(any(String.class))).thenThrow(new UsernameNotFoundException("not exist"));
        assertThrows(BadCredentialsException.class,()->
        {
            ajaxAuthenticationProvider.authenticate(token);
        });

    }

    @Test
    @DisplayName("패스워드 불일치")
    void passwordNotCollect(){
        Mockito.when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(new UserDetailsImpl(member.getEmail(), member.getPassword(), member.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()), member.getNickname(), 10L));
        Mockito.when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);
       assertThrows(BadCredentialsException.class,()->
       {
           ajaxAuthenticationProvider.authenticate(token);
       });




    }


}
