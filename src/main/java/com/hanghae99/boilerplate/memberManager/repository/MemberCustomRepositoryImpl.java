package com.hanghae99.boilerplate.memberManager.repository;

import com.hanghae99.boilerplate.memberManager.model.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.hanghae99.boilerplate.memberManager.model.QMember.member;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Member> findByIdJoinFetch(Long id) {
        return jpaQueryFactory.selectFrom(member)
                .where(member.id.eq(id))
                .innerJoin(member.myBoards)
                .fetchJoin()
                .fetch();
    }
}
