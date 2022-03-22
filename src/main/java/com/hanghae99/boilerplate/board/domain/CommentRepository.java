package com.hanghae99.boilerplate.board.domain;

import com.hanghae99.boilerplate.memberManager.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBoardIdOrderByCreatedAtDesc(Long boardId);

    List<Comment> findAllByMember(Member member);

}
