package com.hanghae99.boilerplate.board.security.jwt.extractor;


import com.hanghae99.boilerplate.security.config.JwtConfig;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;


@Component
public class TokenVerifier {

    @Autowired
    JwtConfig jwtConfig;
    public Jws<Claims> validateToken(String jwtToken, String secretKey) {

        if (jwtToken == null || jwtToken.isBlank()) {
            throw new AuthenticationServiceException("Authorization header cannot be blank");
        }

//      String  jwtToken = header.substring(HEADER_PREFIX.length(), header.length());
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
            throw new ExpiredJwtException(null, null, "Jwt Expired !!");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }




}