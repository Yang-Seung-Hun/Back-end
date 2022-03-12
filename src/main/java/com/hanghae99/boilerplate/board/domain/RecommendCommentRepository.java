package com.hanghae99.boilerplate.board.domain;

import com.hanghae99.boilerplate.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendCommentRepository extends JpaRepository<RecommendComment, Long> {
    Optional<RecommendComment> findByCommentAndMember(Comment comment, Member member);
}
