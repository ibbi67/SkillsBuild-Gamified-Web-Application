package com.example.backend.service;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


/**
 * Service for JWT token generation and validation
 */
@Component
public class JwtService {
    public final SecretKey SECRET_KEY = io.jsonwebtoken.security.Keys.hmacShaKeyFor(
            "ddb1d47e1089c2131d50bf2d74b43b50630228f5e642d74a1d57e84f17d2f11e13c7daac30d748ed92c447b5042487ccdb723782ca14b95a9da203fff010b321".getBytes());
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public JwtService(UserService userService) {
        this.userService = userService;
    }

    public ApiResponse<String> generateToken(String username, String password) {
        User user = userService.findByUsername(username);

        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "User not found");
        if (!bCryptPasswordEncoder.matches(password, user.getPassword()))
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid credentials");

        return ApiResponse.success("Token generated successfully",
                Jwts.builder()
                        .subject(username)
                        .issuedAt(new Date())
                        .expiration(new Date(System.currentTimeMillis() + 86400000))
                        .signWith(SECRET_KEY)
                        .compact());
    }

    /**
     * Validate a token, ensure the request body is a raw string.
     *
     * @param token The token to validate, such as eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huRG9lIiwiaWF0IjoxNzM4MzY3MTc4LCJleHAiOjE3Mzg0NTM1Nzh9.oGO24nf9GvFkZ7VV-D-RV1AHlz_zvfQQiRT7EOY5uQAQ3fts_mHwgbSPqibNn1Eg6g97_8XR8-61n7Etxn2SsA
     * @return true if the token is valid, false otherwise
     */
    public boolean verifyToken(String token) {
        if (token.isBlank()) {
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
