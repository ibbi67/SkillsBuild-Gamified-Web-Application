package com.example.backend.controller;

import com.example.backend.dao.LoginDao;
import com.example.backend.dao.SignupDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.User;
import com.example.backend.service.AuthService;
import com.example.backend.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(JwtService JwtService, AuthService authService) {
        this.jwtService = JwtService;
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupDao signupDao) {
        ApiResponse<Void> response = authService.signup(signupDao);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody LoginDao loginDao,
            HttpServletResponse response) {
        ApiResponse<Void> serviceResponse = authService.login(loginDao);

        if (serviceResponse.getStatus() == HttpStatus.OK.value()) {
            response.addCookie(jwtService.generateCookie(loginDao.getUsername()));
        }

        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getMe(@AuthenticationPrincipal User user) {
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failed(HttpStatus.UNAUTHORIZED.value(), "User not found"));
        return ResponseEntity.ok(ApiResponse.success("User found",
                new User(user.getId(), user.getUsername(), user.getPassword(), user.getRoles())));
    }
}
