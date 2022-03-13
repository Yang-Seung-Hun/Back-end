package com.hanghae99.boilerplate.security.jwt;

import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.BadCredentialsException;

public class RawAccessToken implements JwtToken {

    private String token;

    public RawAccessToken(String token) {
        this.token = token;
    }



    public String getToken() {
        return token;

    }


}
