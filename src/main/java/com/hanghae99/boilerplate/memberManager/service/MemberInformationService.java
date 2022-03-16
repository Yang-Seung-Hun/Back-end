package com.hanghae99.boilerplate.memberManager.service;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MemberInformationService {

    @Autowired
    MemberRepository memberRepository;

    public Map<String, String> getMemberInformaiton(String email){
      log.info("{} request get getMemberInformaiton",email);
        Member member =  memberRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException(email+" cant found"));
        Map<String, String> simpleMemberInformation = new HashMap<String, String>();
        simpleMemberInformation.put("nickname",member.getNickname());
        simpleMemberInformation.put("profileImageUrl",member.getProfileImageUrl());
        return simpleMemberInformation;
    }
}
