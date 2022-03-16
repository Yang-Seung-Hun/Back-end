package com.hanghae99.boilerplate.board.dto;

import com.hanghae99.boilerplate.board.domain.Board;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardRequestDto {
    private String title;
    private String content;
    private String imageUrl;
    private String category;

    public Board toEntity(Member user) {
        return Board.builder()
                .title(this.title)
                .content(this.content)
                .imageUrl(this.imageUrl)
                .category(this.category)
                .member(user)
                .createdAt(LocalDateTime.now())
                .build();

    }
}
