package com.hanghae99.boilerplate.security.jwt.from;

public interface JwtTokenExtractor {
    public String extract(String payload);
}
