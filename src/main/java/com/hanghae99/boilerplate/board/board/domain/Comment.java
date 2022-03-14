package com.hanghae99.boilerplate.board.board.domain;

import com.hanghae99.boilerplate.board.domain.Board;
import com.hanghae99.boilerplate.board.domain.RecommendComment;
import com.hanghae99.boilerplate.board.domain.Reply;
import com.hanghae99.boilerplate.board.dto.CommentResponseDto;
import com.hanghae99.boilerplate.model.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@ToString(exclude = {"replies", "board","member"})
public class Comment {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name ="member_id")
    private Member member;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment", cascade = CascadeType.PERSIST)
    private List<RecommendComment> recommendComments;

    private String content;

    private LocalDateTime createdAt;

    private int recommendCount;

    public void addRecommendCount(){
        this.recommendCount++;
    }

    public CommentResponseDto toDto(){
        return CommentResponseDto.builder()
                .commentId(this.id)
                .content(this.content)
                .memeberId(this.member.getId())
                .createdAt(this.createdAt)
                .recommendCount(this.recommendCount)
                .replyResponseDtoList(this.replies.stream().map(Reply::toDto).collect(Collectors.toList()))
                .build();
    }

    public void subtractRecommendCount() {
        this.recommendCount--;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "comment", cascade = CascadeType.PERSIST)
    private List<Reply> replies;
}
