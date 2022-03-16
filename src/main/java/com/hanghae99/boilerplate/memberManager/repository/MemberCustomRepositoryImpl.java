package com.hanghae99.boilerplate.memberManager.repository;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hanghae99.boilerplate.memberManager.model.QMember.member;

@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Member> findByEmailJoinFetch(String email) {
        return jpaQueryFactory.selectFrom(member)
                .where(member.email.eq(email))
                .innerJoin(member.myBoards)
                .fetchJoin()
                .fetch();
    }
}
