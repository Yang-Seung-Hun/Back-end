package com.hanghae99.boilerplate.security.jwt;

import com.hanghae99.boilerplate.security.jwt.from.JwtToken;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.BadCredentialsException;

public class RawAccessToken implements JwtToken {

    private String token;

    public RawAccessToken(String token) {
        this.token = token;
    }

    public Jws<Claims> parseClaims(String key) {

        try {
            Jws<Claims>   jws = Jwts.parser().setSigningKey(key).parseClaimsJws(this.token);
            return jws;
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            throw new BadCredentialsException("Invaild JWT Token :" + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            throw new BadCredentialsException( ex.getMessage() + " & ExpiredJwtException");
        }
    }

    public String getToken() {
        return token;

    }


}
