package com.hanghae99.boilerplate.board.security.jwt;

public enum Scopes {
    REFRESH_TOKEN;
    public String authority(){
        return this.name();
    }
}
