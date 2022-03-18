package com.hanghae99.boilerplate.signupLogin.kakao.common;

import com.hanghae99.boilerplate.security.model.MemberContext;
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
        public MemberContext registerKakaoUserToMember(TemporaryUser temporaryUser){


            Optional<Member> findMember= memberRepository.findByEmail(temporaryUser.getEmail());


            //존재x
            if(findMember.isEmpty()){
                     Member member=memberRepository.save(new Member(temporaryUser));
                     return new MemberContext(member);
            }
            //존재0
            return new MemberContext(findMember.get());


        }
}
