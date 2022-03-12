package com.hanghae99.boilerplate.security.provider;

import com.hanghae99.boilerplate.model.Role;
import com.hanghae99.boilerplate.security.config.JwtConfig;
import com.hanghae99.boilerplate.security.jwt.JwtAuthenticationToken;
import com.hanghae99.boilerplate.security.jwt.RawAccessToken;
import com.hanghae99.boilerplate.security.jwt.extractor.TokenVerifier;
import com.hanghae99.boilerplate.security.model.MemberContext;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.DefaultJwtSigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private TokenVerifier tokenVerifier;



    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RawAccessToken rawAccessToken = (RawAccessToken) authentication.getCredentials();
        Jws<Claims> jwsClaims;
        try {
            jwsClaims = tokenVerifier.validateToken(rawAccessToken.getToken(), jwtConfig.getTokenSigningKey());

            Long memberId = jwsClaims.getBody().get("memberId", Long.class);

            String sub = jwsClaims.getBody().getSubject();
            List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
            List<GrantedAuthority> authorityList = scopes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            MemberContext context = MemberContext.create(memberId, sub, authorityList);
            //새로운 토큰을 발급해준다
            return new JwtAuthenticationToken(context, context.getAuthorities());
        } catch (SignatureException e) {
            throw new SignatureException(e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new JwtException(e.getMessage());
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null,null,e.getMessage());
        }
    }




    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
