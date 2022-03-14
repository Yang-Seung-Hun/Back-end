package com.hanghae99.boilerplate.board.domain;

import java.util.List;

public interface BoardCustomRepository {

    List<Board> findByIdJoinFetch(Long boardId);
}
