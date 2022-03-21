package com.hanghae99.boilerplate.unitTest.signupLogin.SignupLogin.kakao.common;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberCustomRepository;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import com.hanghae99.boilerplate.signupLogin.kakao.common.RegisterMember;
import com.hanghae99.boilerplate.signupLogin.kakao.service.KakaoLoginService;
import com.hanghae99.boilerplate.signupLogin.service.SignupLoginService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RegisterMemberTest {


    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    RegisterMember registerMember = new RegisterMember();

    TemporaryUser temporaryUser = new TemporaryUser("wns674@naver.com","ghwns","123");
    @Test
    @DisplayName("존재하는 유저를 등록")
    void registerMemberExist(){
        Mockito.when(memberRepository.findByEmail(temporaryUser.getEmail())).thenReturn(Optional.of(new Member(temporaryUser)));
        MemberContext memberContext =  registerMember.registerKakaoUserToMember(temporaryUser);
        Assertions.assertEquals(memberContext.getNickname(),temporaryUser.getNickname());
    }

    @Test
    @DisplayName("존재x인 유저를 등록")
    void registerMemberNotExist(){
        Mockito.when(memberRepository.findByEmail(temporaryUser.getEmail())).thenReturn(Optional.empty());
        Mockito.when(memberRepository.save(any(Member.class))).thenReturn(new Member(temporaryUser));

        MemberContext memberContext =  registerMember.registerKakaoUserToMember(temporaryUser);
        Assertions.assertEquals(memberContext.getNickname(),temporaryUser.getNickname());
    }



}
