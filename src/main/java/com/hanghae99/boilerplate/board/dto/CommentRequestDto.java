package com.hanghae99.boilerplate.board.dto;

import lombok.Getter;


@Getter
public class CommentRequestDto {

   private Long boardId;
   private String content;

}
