package com.hanghae99.boilerplate.security.jwt.extractor;


import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;


@Component
public class TokenVerifier {


    public Jws<Claims> validateToken(String jwtToken, String secretKey) {


        Jws<Claims> claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return claims;
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, "Jwt Expired !!");
        }

    }
//
//    catch (UnsupportedJwtException|MalformedJwtException|SignatureException|IllegalArgumentException e) {
//        throw new JwtException(e.getMessage());
//    }


}