package com.example.backend.controller;

import com.example.backend.dao.LoginDao;
import com.example.backend.dao.SignupDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.User;
import com.example.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Signup a new user")
    public ResponseEntity<ApiResponse<Void>> signup(
        @Valid @RequestBody SignupDao signupDao,
        HttpServletResponse response
    ) {
        ApiResponse<Void> signupResponse = authService.signup(signupDao, response);
        return ResponseEntity.status(signupResponse.getStatus()).body(signupResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "Login an existing user")
    public ResponseEntity<ApiResponse<Void>> login(
        @Valid @RequestBody LoginDao loginDao,
        HttpServletResponse response
    ) {
        ApiResponse<Void> serviceResponse = authService.login(loginDao, response);
        return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse);
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current user")
    public ResponseEntity<ApiResponse<User>> getMe(
        @CookieValue(value = "access_token", required = false) String access_token
    ) {
        ApiResponse<User> meResponse = authService.me(access_token);
        return ResponseEntity.status(meResponse.getStatus()).body(meResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh the access token using the refresh token")
    public ResponseEntity<ApiResponse<Void>> refresh(
        @CookieValue(value = "refresh_token", required = false) String refreshToken,
        HttpServletResponse response
    ) {
        ApiResponse<Void> refreshResponse = authService.refresh(refreshToken, response);
        return ResponseEntity.status(response.getStatus()).body(refreshResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout the current user")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        ApiResponse<Void> logoutResponse = authService.logout(response);
        return ResponseEntity.status(response.getStatus()).body(logoutResponse);
    }
}
