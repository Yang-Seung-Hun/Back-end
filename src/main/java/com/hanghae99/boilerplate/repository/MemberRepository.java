package com.hanghae99.boilerplate.repository;

import com.hanghae99.boilerplate.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member , Long> {
    Optional<Member> findByEmail(String email);
}
