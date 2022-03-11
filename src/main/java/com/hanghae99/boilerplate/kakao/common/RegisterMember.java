package com.hanghae99.boilerplate.kakao.common;

import com.hanghae99.boilerplate.dto.responseDto.LoginResponseDto;
import com.hanghae99.boilerplate.kakao.TemporaryUser;
import com.hanghae99.boilerplate.model.Member;
import com.hanghae99.boilerplate.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RegisterMember {

        @Autowired
        private MemberRepository memberRepository;

        public Optional<LoginResponseDto> registerKakaoUserToMember(TemporaryUser temporaryUser){
            Member member = new Member(temporaryUser);

            LoginResponseDto loginResponseDto = new LoginResponseDto(member.getEmail(), member.getNickname(),member.getRoles());

           if (!memberRepository.findByEmail(temporaryUser.getEmail()).isPresent()){
               memberRepository.save(member);
           }

           return Optional.of(loginResponseDto);

        }
}
