package com.example.backend.controller;

import com.example.backend.domain.User;
import com.example.backend.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Invalid request body");
        }

        if (user.getUsername().equals("johnDoe") && user.getPassword().equals("password123")) {
            String token = JwtService.generateToken(user.getUsername());
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    /**
     * Validate a token, ensure the request body is a raw string.
     *
     * @param token The token to validate, such as eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huRG9lIiwiaWF0IjoxNzM4MzY3MTc4LCJleHAiOjE3Mzg0NTM1Nzh9.oGO24nf9GvFkZ7VV-D-RV1AHlz_zvfQQiRT7EOY5uQAQ3fts_mHwgbSPqibNn1Eg6g97_8XR8-61n7Etxn2SsA
     * @return A string indicating whether the token is valid or not
     */
    @PostMapping("/validate")
    public String validateToken(@RequestBody String token) {
        Claims claims = JwtService.verifyToken(token);
        if (claims != null) {
            return "Token is valid";
        } else {
            return "Token is invalid";
        }
    }
}
