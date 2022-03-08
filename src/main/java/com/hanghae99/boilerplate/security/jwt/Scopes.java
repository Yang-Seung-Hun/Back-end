package com.hanghae99.boilerplate.security.jwt;

public enum Scopes {
    REFRESH_TOKEN;
    public String authority(){
        return this.name();
    }
}
