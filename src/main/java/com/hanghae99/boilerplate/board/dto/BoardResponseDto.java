package com.hanghae99.boilerplate.board.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class BoardResponseDto implements Serializable {
    private Long id;

    private String title;
    private String nickname;
    private String profileImageUrl;



    private String content;
    private String imageUrl;

    private int agreeCount;
    private int disagreeCount;
    private int recommendCount;
    private LocalDateTime createdAt;

    private String category;

    private String userStatus; //없으, 찬성, 반대




}
