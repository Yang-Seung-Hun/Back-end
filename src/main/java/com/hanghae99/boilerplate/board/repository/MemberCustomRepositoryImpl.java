package com.hanghae99.boilerplate.board.repository;

import com.hanghae99.boilerplate.model.Member;
import com.hanghae99.boilerplate.repository.MemberCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hanghae99.boilerplate.model.QMember.member;

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
