package com.hanghae99.boilerplate.security.jwt.extractor;


import io.jsonwebtoken.*;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;


@Component
public class TokenVerifier {

    public static String HEADER_PREFIX = "Bearer ";


    public Jws<Claims> validateToken(String jwtToken, String secretKey) {

        if (jwtToken == null || jwtToken.isBlank()) {
            throw new AuthenticationServiceException("Authorization header cannot be blank");
        }
        if (jwtToken.length() < HEADER_PREFIX.length()) {
            throw new AuthenticationServiceException("Wrong Token format");
        }
        jwtToken = jwtToken.substring(HEADER_PREFIX.length(), jwtToken.length());
        Jws<Claims> claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return claims;
        } catch (UnsupportedJwtException e) {
            throw new JwtException(e.getMessage());
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException(e.getMessage());
        } catch (SignatureException e) {
            throw new SignatureException(e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new JwtException("Jwt Expired !!");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}