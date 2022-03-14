package com.hanghae99.boilerplate.board.board.domain;

import com.hanghae99.boilerplate.board.domain.MyBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyBoardRepository extends JpaRepository<MyBoard, Long> {
}
