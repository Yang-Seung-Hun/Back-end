package com.hanghae99.boilerplate.signupLogin.test;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.config.RefreshTokenRedis;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RefreshTokenRedis redis;

    @Transactional
    public MemberContext signupRequest(SignupReqestDto signupReqestDto) {
        signupReqestDto.setPassword(passwordEncoder.encode(signupReqestDto.getPassword()));
        Member member= memberRepository.save(new Member(signupReqestDto));
        return new MemberContext(member);
    }
}
