package com.hanghae99.boilerplate.security.service;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    MemberRepository memberRepository;


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email).orElseThrow(
                ()-> new UsernameNotFoundException("Not found user -> "+email)
        );
        return new UserDetailsImpl(member.getEmail(),member.getPassword(),member.getRoles().stream().map( role ->
                new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()));
    }
}
