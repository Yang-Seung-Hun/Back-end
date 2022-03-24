package com.hanghae99.boilerplate.memberManager.model;

import com.hanghae99.boilerplate.board.domain.*;
import com.hanghae99.boilerplate.signupLogin.dto.requestDto.SignupReqestDto;
import com.hanghae99.boilerplate.signupLogin.kakao.TemporaryUser;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Member implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id ;

    private String email;

    private String password;

    private String nickname;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles =new HashSet<>();

    private String profileImageUrl;

    private String description;

    public Member(SignupReqestDto signupReqestDto){
        this.email=signupReqestDto.getEmail();
        this.password= signupReqestDto.getPassword();
        this.nickname=signupReqestDto.getNickname();
        this.profileImageUrl = signupReqestDto.getProfileImageUrl();
        this.roles.add(Role.USER);
    }

    public Member(TemporaryUser temporaryUser){
        this.email = temporaryUser.getEmail();
        this.profileImageUrl= temporaryUser.getProfileImageUrl();
        this.nickname= temporaryUser.getNickname();
        this.password="0000";
        this.roles.add(Role.USER);
        this.roles.add(Role.KAKAO);

    }
    public void setPassword(String password){
        this.password= password;
    }

    //추가
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
    private List<Board> boards;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
    private List<Comment> comments;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
    private List<Vote> votes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
    private List<RecommendBoard> recommendBoards;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
    private List<RecommendComment> recommendComments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
    private List<MyBoard> myBoards;





}
