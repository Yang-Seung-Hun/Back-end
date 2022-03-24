package com.hanghae99.boilerplate.board.domain;

import com.hanghae99.boilerplate.board.dto.ReplyResponseDto;
import com.hanghae99.boilerplate.memberManager.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name ="member_id")
    private Member member;
//    private Long memberId;

    @ManyToOne
    @JoinColumn(name ="comment_id")
    private Comment comment;

    private String content;

    private LocalDateTime createdAt;

    private int recommendCount;

    public ReplyResponseDto toDto(){
        return ReplyResponseDto.builder()
                .replyId(this.id)
                .commentId(this.comment.getId())
                .content(this.content)
                .memberId(this.member.getId())
                .nickname(this.member.getNickname())
                .profileImageUrl(this.member.getProfileImageUrl())
                .recommendCount(this.recommendCount)
                .createdAt(this.createdAt)
                .build();
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reply", cascade = CascadeType.PERSIST)
    private List<RecommendReply> recommendReplies;

    public void addRecommendCount() {
        this.recommendCount++;

    }

    public void subtractRecommendCount() {
        this.recommendCount--;
    }


}