package com.hanghae99.boilerplate.security.jwt;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor
public class AccessToken implements JwtToken {

    private String  rawToken;

    @Autowired
    @JsonIgnore
    private Claims claims;


    @Override
    public String getToken() {
        return this.rawToken;
    }
}
