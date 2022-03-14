package com.hanghae99.boilerplate.board.board.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardSearchDto {
    private Long id;

    private String title;
//    private String nickname;
//    private String profileImageUrl;



    private String content;
    private String image_url;

    private int agree_count;
    private int disagree_count;
    private int recommend_count;
    private LocalDateTime created_at;

    private String category;

    private Long member_id;
}
