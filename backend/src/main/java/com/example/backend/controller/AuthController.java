package com.example.backend.controller;

import com.example.backend.dao.LoginDao;
import com.example.backend.dao.SignupDao;
import com.example.backend.domain.User;
import com.example.backend.service.AuthService;
import com.example.backend.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService JwtService;
    private final AuthService authService;

    public AuthController(JwtService JwtService, AuthService authService) {
        this.JwtService = JwtService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDao loginDao) {
        if (loginDao.getUsername() == null || loginDao.getPassword() == null) {
            return ResponseEntity.badRequest().body("Invalid request body");
        }

        String token = JwtService.generateToken(loginDao.getUsername(), loginDao.getPassword());

        if (token != null) {
            return ResponseEntity.ok(token);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupDao signupDao) {
        if (signupDao.getUsername() == null || signupDao.getPassword() == null) {
            return ResponseEntity.badRequest().body("Invalid request body");
        }

        User user = authService.signup(signupDao);

        if (user != null) {
            return ResponseEntity.ok("User created successfully");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create user");
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestBody String token) {
        if (token == null) {
            return ResponseEntity.badRequest().body("Invalid request body");
        }

        boolean isValid = JwtService.verifyToken(token);

        if (isValid) {
            return ResponseEntity.ok("Token is valid");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
    }
}
