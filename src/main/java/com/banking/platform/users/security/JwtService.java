package com.banking.platform.users.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JwtService {

    private final SecretKey key;
    private final long ttlMillies;

    public JwtService (@Value("${security.jwt.secret}") String secret , @Value("${security.jwt.ttl-minutes:60}") long ttlMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttlMillies = ttlMinutes * 60_000;
    }

    public String issue (UUID userId , UUID tenantId , List<String> roles) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userId.toString())
                .claim("tenantId" , tenantId.toString())
                .claim("roles" , roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttlMillies))
                .signWith(key)
                .compact();
    }

    public Claims parse (String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
