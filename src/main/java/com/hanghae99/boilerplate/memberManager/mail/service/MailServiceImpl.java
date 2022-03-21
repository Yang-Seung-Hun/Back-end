package com.hanghae99.boilerplate.memberManager.mail.service;

import com.hanghae99.boilerplate.memberManager.mail.platforms.Google;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.security.SecureRandom;

@Service
public class MailServiceImpl implements MailService {


    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    private int MIN_SIZE = 10000;
    private int MAX_SIZE = 1000000000;

    private final String title = "안녕하세요.Boilerplate입니다 ";

    Google google = new Google();


    @Value(("mail.ExpireTmie"))
    private String expireTime;
    private SecureRandom random = new SecureRandom();


    public String makeText(String key) {
        return "<a>" + "http://" + "/api/set/password/" + key + "</a>";
    }


    @Transactional
    @Override
    public void sendPasswordEmail(String email) throws MessagingException {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + "not exist"));
        String key = String.valueOf(random.nextInt((MAX_SIZE - MIN_SIZE) + 1) + MIN_SIZE);
        member.setPassword(passwordEncoder.encode(key));
        google.sendMail(email, title, makeText(key));

    }


}
