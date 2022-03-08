package com.hanghae99.boilerplate.security.jwt.extractor;


import com.hanghae99.boilerplate.security.jwt.from.JwtTokenExtractor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;

@Component
public class TokenExtractor implements JwtTokenExtractor {
    public static String HEADER_PREFIX ="Bearer ";


    @Override
    public String extract(String payload) {

       if(payload == null ||payload.isBlank()){
           throw new AuthenticationServiceException("Authorization header cannot be blank");
       }
       if(payload.length()<HEADER_PREFIX.length()){
           throw new AuthenticationServiceException("Wrong Token format");
       }
        return payload.substring(HEADER_PREFIX.length(),payload.length());
    }
}
