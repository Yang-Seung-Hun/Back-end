package com.hanghae99.boilerplate.board.board.domain;


import com.hanghae99.boilerplate.board.domain.Comment;
import com.hanghae99.boilerplate.board.domain.RecommendBoard;
import com.hanghae99.boilerplate.board.domain.Vote;
import com.hanghae99.boilerplate.board.dto.BoardResponseDto;
import com.hanghae99.boilerplate.model.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@Setter
//@Document(indexName="boards")
public class Board {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    private String title;
    private String content;
    private String imageUrl;



    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    private int agreeCount;
    private int disagreeCount;
    private int recommendCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Comment> comments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<RecommendBoard> recommendBoards;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Vote> votes;


    public void addAgreeCount(){
        this.agreeCount++;
    }

    public void addDisagreeCount(){
        this.disagreeCount++;
    }

    public void addRecommendCount(){
        this.recommendCount++;
    }

    public void subtractAgreeCount(){
        this.agreeCount--;
    }

    public void subtractDisagreeCount(){
        this.disagreeCount--;
    }

    public void subtractRecommendCount(){
        this.recommendCount--;
    }
    public BoardResponseDto toCreatedDto() {
        System.out.println(this.content);
        System.out.println(this.member);

        return  BoardResponseDto.builder()
                .id(this.id)
                .title(this.title)
                .nickname(this.member.getNickname())
                .profileImageUrl(this.member.getProfileImageUrl())
                .content(this.content)
                .imageUrl(this.imageUrl)
                .agreeCount(this.agreeCount)
                .disagreeCount(this.disagreeCount)
                .recommendCount(this.recommendCount)
                .createdAt(this.createdAt)
                .category(this.category)
                .build();
    }

}
