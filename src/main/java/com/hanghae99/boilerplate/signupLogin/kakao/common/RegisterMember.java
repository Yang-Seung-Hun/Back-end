package com.hanghae99.boilerplate.signupLogin.kakao.common;

import com.hanghae99.boilerplate.signupLogin.dto.responseDto.LoginResponseDto;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Slf4j
public class RegisterMember {

        @Autowired
        private MemberRepository memberRepository;

        @Transactional
        public Optional<LoginResponseDto> registerKakaoUserToMember(TemporaryUser temporaryUser){
            Member member = new Member(temporaryUser);

            LoginResponseDto loginResponseDto = new LoginResponseDto(member.getEmail(), member.getNickname(),member.getRoles());

           if (!memberRepository.existsMemberByEmail(temporaryUser.getEmail())){
               log.info("kakao signup user email : {}",loginResponseDto.getEmail());
               memberRepository.save(member);
           }

           return Optional.of(loginResponseDto);

        }
}
