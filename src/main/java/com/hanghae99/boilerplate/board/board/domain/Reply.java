package com.hanghae99.boilerplate.board.board.domain;

import com.hanghae99.boilerplate.board.domain.Comment;
import com.hanghae99.boilerplate.board.dto.ReplyResponseDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@ToString(exclude = "comment")
public class Reply {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

//    @ManyToOne
//    @JoinColumn(name ="member_id")
//    private Member member;
    private Long memberId;

    @ManyToOne
    @JoinColumn(name ="comment_id")
    private Comment comment;

    private String content;

    private LocalDateTime createdAt;

    public ReplyResponseDto toDto(){
        return ReplyResponseDto.builder()
                .replyId(this.id)
                .commentId(this.comment.getId())
                .content(this.content)
                .memberId(this.memberId)
                .createdAt(this.createdAt)
                .build();
    }


}