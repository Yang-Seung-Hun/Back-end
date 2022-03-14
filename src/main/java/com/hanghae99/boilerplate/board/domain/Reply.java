package com.hanghae99.boilerplate.board.domain;

import com.hanghae99.boilerplate.board.dto.ReplyResponseDto;
import com.hanghae99.boilerplate.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import lombok.ToString;

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