package com.example.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Service for JWT token generation and validation
 */
@Component
public class JwtService {

    public static final String SECRET = "ddb1d47e1089c2131d50bf2d74b43b50630228f5e642d74a1d57e84f17d2f11e13c7daac30d748ed92c447b5042487ccdb723782ca14b95a9da203fff010b321";
    public static final SecretKey SECRET_KEY = io.jsonwebtoken.security.Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims verifyToken(String token) {
        try {
            return Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
