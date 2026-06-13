package com.server.app.config;

import com.server.app.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JsonWebToken {
    @Value("${security.jwt.expiration-time}")
    private int tokenTime;

    @Value("${security.jwt.secret-key}")
    private String tokenSecret;

    private SecretKey getTokenKey() {
        byte[] keyBytes = Decoders.BASE64.decode(tokenSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(User user) {
        System.out.println("SECRET LENGTH: " + tokenSecret.length());
        try {
            SecretKey key = getTokenKey();
            System.out.println("KEY OK, bytes: " + key.getEncoded().length);
        } catch (Exception e) {
            System.out.println("ERROR EN KEY: " + e.getMessage());
        }
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("id", user.getId());
        return Jwts.builder()
                .claims(json)
                .signWith(getTokenKey())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenTime))
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getTokenKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return null;
        }
    }

    public Integer extractIdUser(String token) {
        Claims claims = extractClaims(token);
        if (claims == null) {
            return null;
        }
        return claims.get("id", Integer.class);
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractClaims(token);
        return claims == null || claims.getExpiration().before(new Date());
    }
}
