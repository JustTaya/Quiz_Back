package com.quiz.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class TokenProvider {

    @Value("${quize.security.secret:Default}")
    private String secret;
    @Value("${quize.security.token-expiration-msec:464000000}")
    private long tokenExpirationMsec;

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.warn("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.warn("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string is empty.");
        }
        return false;
    }

    public String getUserEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
    public String createToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpirationMsec);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
