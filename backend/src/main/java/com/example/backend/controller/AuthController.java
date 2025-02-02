package com.example.backend.controller;

import com.example.backend.dao.LoginDao;
import com.example.backend.dao.SignupDao;
import com.example.backend.domain.User;
import com.example.backend.service.AuthService;
import com.example.backend.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<String> login(@Valid @RequestBody LoginDao loginDao) {
        String token = JwtService.generateToken(loginDao.getUsername(), loginDao.getPassword());
        if (token == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials");

        return ResponseEntity.ok(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupDao signupDao) {
        User user = authService.signup(signupDao);
        if (user == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");

        return ResponseEntity.ok("User created successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        return ResponseEntity.ok(user);
    }
}
