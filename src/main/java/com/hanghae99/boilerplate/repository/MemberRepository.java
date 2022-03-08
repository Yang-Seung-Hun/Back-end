package com.hanghae99.boilerplate.repository;

import com.hanghae99.boilerplate.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member , Long> {
    Optional<Member> findByEmail(String email);

    @Query("select m.nickname from Member m " +
            "where " +
            "m.email=:email")
    Optional<String>  getNickname(@Param("email")String email);
}
