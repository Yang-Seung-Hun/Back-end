package com.hanghae99.boilerplate.board.board.dto;

import com.hanghae99.boilerplate.board.dto.ReplyResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long commentId;
    private Long memeberId;
    private String content;

    private LocalDateTime createdAt;
    private int recommendCount;

    private List<ReplyResponseDto> replyResponseDtoList;

}
