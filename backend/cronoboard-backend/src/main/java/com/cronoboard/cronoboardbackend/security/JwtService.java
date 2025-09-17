package com.cronoboard.cronoboardbackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtService {

    private final Key key;
    private final long expiryMillis;

    public JwtService(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.expiresInSeconds:3600}") long expiresInSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiryMillis = expiresInSeconds * 1000L;
    }

    public String generateToken(String email, Long userId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(email)
            .claim("uid", userId)
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expiryMillis))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public long getDefaultExpirySeconds() {
        return expiryMillis / 1000L;
    }
}
