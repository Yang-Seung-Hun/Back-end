package com.hanghae99.boilerplate.memberManager.mail.service;

import com.hanghae99.boilerplate.memberManager.mail.MailVerifyRedis;
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
import javax.security.sasl.AuthenticationException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    MailVerifyRedis mailVerifyRedis;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private static int MIN_SIZE = 10000;
    private static int MAX_SIZE = 1000000000;

    private final String title = "안녕하세요.Boilerplate입니다 ";

    Google google =new Google();


    @Value(("mail.ExpireTmie"))
    private String expireTime;
    private SecureRandom random = new SecureRandom();


    public String makeText(String key){
        return "<a>"+  "http://"+  "/api/set/password/"+key+ "</a>";
    }


    @Override
    public Optional<String> verifyEmail(String email) {
        if (!memberRepository.existsMemberByEmail(email)) //존재xx
            return Optional.empty();
        String key = String.valueOf(random.nextLong());

        return Optional.of(key);
    }

    @Override
    public Optional<Member> verifyKey(String key) {
        String email  =   mailVerifyRedis.getData(key);
        if(email==null)
            return Optional.empty();
        Optional<Member> member=  memberRepository.findByEmail(email);
        if(member.isEmpty())
            return Optional.empty();
        return member;

    }


    public void sendFindPasswordVerifyMail(String email) throws MessagingException {
        String key = verifyEmail(email).orElseThrow(()->new UsernameNotFoundException(email +"not exist") );
          mailVerifyRedis.setExpire(key,email, Long.parseLong(expireTime));
           google.sendMail(email,title,makeText(key));

    }


    @Transactional
    public String  isOkGiveNewPassword(String key) throws AuthenticationException {
       Member member =  verifyKey(key).orElseThrow(()-> new AuthenticationException("Bad Access"));

        random.setSeed(new Date().getTime());
        String  randomNum =String.valueOf(random.nextInt((MAX_SIZE - MIN_SIZE) + 1) + MIN_SIZE);
       member.setPassword(passwordEncoder.encode(randomNum));
       return  randomNum;
    }




}
