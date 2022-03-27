package com.hanghae99.boilerplate.memberManager.repository;

import com.hanghae99.boilerplate.memberManager.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>  ,MemberCustomRepository {



    Optional<Member> findByEmail(String email);

    @Query("select m.nickname from Member m " +
            "where " +
            "m.email = :email")
    Optional<String>  getNickname(@Param("email")String email);

    void deleteByEmail(String email);



    boolean existsMemberByEmail(String email);

    boolean existsMemberByNickname(String nickname);



}
