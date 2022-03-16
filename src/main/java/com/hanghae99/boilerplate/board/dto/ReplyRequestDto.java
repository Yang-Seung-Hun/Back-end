package com.hanghae99.boilerplate.board.dto;

import lombok.Getter;

@Getter
public class ReplyRequestDto {
    private Long commentId;

    private String content;
}
