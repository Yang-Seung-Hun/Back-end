package com.hanghae99.boilerplate.security.jwt.extractor;


import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class TokenVerifier {

    public Jws<Claims>  validateToken(String jwtToken, String secretKey) {
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