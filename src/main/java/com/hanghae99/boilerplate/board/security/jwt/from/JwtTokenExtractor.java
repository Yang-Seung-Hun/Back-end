package com.hanghae99.boilerplate.board.security.jwt.from;

public interface JwtTokenExtractor {
    public String extract(String payload);
}
