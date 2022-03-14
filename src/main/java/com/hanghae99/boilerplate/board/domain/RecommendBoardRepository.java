package com.hanghae99.boilerplate.board.domain;

import com.hanghae99.boilerplate.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendBoardRepository extends JpaRepository<RecommendBoard, Long> {
    Optional<RecommendBoard> findByBoardAndMember(Board board, Member member);
}
