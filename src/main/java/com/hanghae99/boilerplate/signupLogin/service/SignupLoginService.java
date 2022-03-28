package com.hanghae99.boilerplate.signupLogin.service;


import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.config.RefreshTokenRedis;
import com.hanghae99.boilerplate.security.model.MemberContext;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Service
public class SignupLoginService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RefreshTokenRedis redis;

    @Transactional
    public MemberContext signupRequest(SignupReqestDto signupReqestDto) {
        Member member = memberRepository.save(new Member(signupReqestDto, passwordEncoder.encode(signupReqestDto.getPassword())));
        return new MemberContext(member);
    }

    @Transactional(readOnly = true)
    public void membershipWithdrawal(MemberContext memberContext, String password) {
        Member member = memberRepository.findById(memberContext.getMemberId()).orElseThrow(() -> new UsernameNotFoundException(memberContext.getUsername() + " not exist"));
        if (isSamePassword(password, member.getPassword())) {
            memberRepository.deleteById(member.getId());
        }
    }

    public boolean isSamePassword(String inputPW, String savedPassword) {
        return passwordEncoder.matches(inputPW, savedPassword);
    }

    public void logoutRequest(HttpServletRequest request) throws IOException {
        Arrays.stream(request.getCookies()).anyMatch(cookie -> removeCookieIfSame(cookie));

    }

    public boolean removeCookieIfSame(Cookie cookie) {
        if (cookie.getName().equals(JwtConfig.AUTHENTICATION_HEADER_NAME)) {
            redis.removeData(cookie.getValue());
            return true;
        }
        return false;
    }


    public boolean DuplicatesEmail(String email) {
        return memberRepository.existsMemberByEmail(email);
    }

    public boolean DuplicatePassword(String nickname) {
        return memberRepository.existsMemberByNickname(nickname);
    }
}
