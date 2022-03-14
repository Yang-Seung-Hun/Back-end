package com.hanghae99.boilerplate.board.board.domain;

import com.hanghae99.boilerplate.board.domain.Board;
import com.hanghae99.boilerplate.board.domain.BoardCustomRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.hanghae99.boilerplate.board.domain.QBoard.board;

@Repository
public class BoardCustomRepositoryImpl implements BoardCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public BoardCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Board> findByIdJoinFetch(Long boardId) {
        return jpaQueryFactory.selectFrom(board)
                .where(board.id.eq(boardId))
                .innerJoin(board.comments)
                .fetchJoin()
                .fetch();
    }
}
