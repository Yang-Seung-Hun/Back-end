package com.hanghae99.boilerplate.model;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;


@Entity
@Getter
@EqualsAndHashCode(of = "id")
public class Member {
    @Id @GeneratedValue
    private Long id ;

    private String email;

    private String password;

    private String nickname;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
}
