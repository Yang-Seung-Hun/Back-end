package com.hanghae99.boilerplate.board.board.domain;

import com.hanghae99.boilerplate.board.domain.Board;

import java.util.List;

public interface BoardCustomRepository {

    List<Board> findByIdJoinFetch(Long boardId);
}
