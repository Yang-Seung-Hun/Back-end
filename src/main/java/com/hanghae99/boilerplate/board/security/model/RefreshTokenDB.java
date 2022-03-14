package com.hanghae99.boilerplate.board.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "refreshToken")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDB {

    @Id
    @Column(name="email")
    private String email;
    @Column(nullable = false,length = 400)
    private String token;
}
