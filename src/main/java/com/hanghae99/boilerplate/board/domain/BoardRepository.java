package com.hanghae99.boilerplate.board.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardCustomRepository{
    Page<Board> findAll(Pageable pageable);
    List<Board> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<Board> findAllByCategory(String categoryName);

    List<Board> findByTitleContains(String name);

//    @Query("select DISTINCT b from Board b join fetch b.comments")
//    Board findByIdJoinFetch(Long boardId);
}
