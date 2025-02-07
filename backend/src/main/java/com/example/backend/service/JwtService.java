package com.example.backend.service;

import com.example.backend.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Service for JWT token generation and validation
 */
@Component
public class JwtService {

    public final SecretKey SECRET_KEY = io.jsonwebtoken.security.Keys.hmacShaKeyFor(
        "ddb1d47e1089c2131d50bf2d74b43b50630228f5e642d74a1d57e84f17d2f11e13c7daac30d748ed92c447b5042487ccdb723782ca14b95a9da203fff010b321".getBytes()
    );
    private final UserService userService;
    private final long ACCESS_TOKEN_EXPIRATION_SECOND = 60 * 15; // 15 minutes
    private final long REFRESH_TOKEN_EXPIRATION_SECOND = 60 * 60 * 24 * 365; // 365 days

    @Autowired
    public JwtService(UserService userService) {
        this.userService = userService;
    }

    public Cookie generateLogoutAccessCookie() {
        Cookie cookie = new Cookie("access_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(0);
        return cookie;
    }

    public Cookie generateLogoutRefreshCookie() {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge(0);
        return cookie;
    }

    public String generateAccessToken(String username) {
        return generateToken(username, ACCESS_TOKEN_EXPIRATION_SECOND * 1000);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, REFRESH_TOKEN_EXPIRATION_SECOND * 1000);
    }

    public Cookie generateAccessTokenCookie(String username) {
        return generateCookie(username, ACCESS_TOKEN_EXPIRATION_SECOND, "access_token");
    }

    public Cookie generateRefreshTokenCookie(String username) {
        return generateCookie(username, REFRESH_TOKEN_EXPIRATION_SECOND, "refresh_token");
    }

    private String generateToken(String username, long expiration) {
        return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(SECRET_KEY)
            .compact();
    }

    private Cookie generateCookie(String username, long expiration, String cookieName) {
        String token = generateToken(username, expiration * 1000);
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        cookie.setMaxAge((int) expiration);
        return cookie;
    }

    /**
     * Validate a token, ensure the request body is a raw string.
     *
     * @param token The token to validate, such as eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huRG9lIiwiaWF0IjoxNzM4MzY3MTc4LCJleHAiOjE3Mzg0NTM1Nzh9.oGO24nf9GvFkZ7VV-D-RV1AHlz_zvfQQiRT7EOY5uQAQ3fts_mHwgbSPqibNn1Eg6g97_8XR8-61n7Etxn2SsA
     * @return true if the token is valid, false otherwise
     */
    public boolean verifyToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
        } catch (JwtException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public User getUserDetails(String token) {
        Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token).getPayload();
        String username = claims.getSubject();
        return userService.findByUsername(username);
    }
}
