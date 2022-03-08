package com.hanghae99.boilerplate.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.List;
import java.util.Optional;

public class RefreshToken {
    private Jws<Claims> claimsJws;

    private RefreshToken(Jws<Claims> claimsJws) {
        this.claimsJws = claimsJws;
    }

    public static Optional<RefreshToken> create(RawAccessToken token, String key) {
        Jws<Claims> claims = token.parseClaims(key);

        List<String> scopes = claims.getBody().get("scope", List.class);

        if (scopes == null || scopes.isEmpty() ||
                !scopes.stream().filter(s -> Scopes.REFRESH_TOKEN.authority().equals(s)).findFirst().isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new RefreshToken(claims));
    }

    public Jws<Claims> getClaimsJws() {
        return claimsJws;
    }

    public String getJti() {
        return claimsJws.getBody().getId();
    }

    public String getSubject() {
        return claimsJws.getBody().getSubject();
    }


}
