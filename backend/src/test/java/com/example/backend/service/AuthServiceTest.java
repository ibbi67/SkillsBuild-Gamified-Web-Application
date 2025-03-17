package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.backend.dao.LoginDao;
import com.example.backend.dao.SignupDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.User;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private StreaksService streaksService;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private SignupDao signupDao;
    private LoginDao loginDao;
    private User user;

    @BeforeEach
    void setUp() {
        signupDao = new SignupDao("testuser", "testpassword");
        loginDao = new LoginDao("testuser", "testpassword");
        user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("testpassword"));
    }

    @Test
    void signup_WithNewUser_ShouldReturnSuccess() {
        // Arrange
        when(userService.save(any(User.class))).thenReturn(user);
        when(jwtService.generateRefreshTokenCookie(anyString())).thenReturn(null);
        when(jwtService.generateAccessTokenCookie(anyString())).thenReturn(null);

        // Act
        ApiResponse<Void> response = authService.signup(signupDao, this.response);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("User created successfully", response.getMessage());
        verify(userService, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateRefreshTokenCookie(anyString());
        verify(jwtService, times(1)).generateAccessTokenCookie(anyString());
    }

    @Test
    void signup_WithExistingUser_ShouldReturnFailure() {
        // Arrange
        when(userService.save(any(User.class))).thenReturn(null);

        // Act
        ApiResponse<Void> response = authService.signup(signupDao, this.response);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("User already exists", response.getMessage());
        verify(userService, times(1)).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnSuccess() {
        // Arrange
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(jwtService.generateRefreshTokenCookie(anyString())).thenReturn(null);
        when(jwtService.generateAccessTokenCookie(anyString())).thenReturn(null);

        // Act
        ApiResponse<Void> response = authService.login(loginDao, this.response);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Login successful", response.getMessage());
        verify(userService, times(1)).findByUsername(anyString());
        verify(jwtService, times(1)).generateRefreshTokenCookie(anyString());
        verify(jwtService, times(1)).generateAccessTokenCookie(anyString());
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnFailure() {
        // Arrange
        when(userService.findByUsername(anyString())).thenReturn(user);

        // Act
        ApiResponse<Void> response = authService.login(new LoginDao("testuser", "wrongpassword"), this.response);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid credentials", response.getMessage());
        verify(userService, times(1)).findByUsername(anyString());
    }

    @Test
    void login_WithNonExistentUser_ShouldReturnFailure() {
        // Arrange
        when(userService.findByUsername(anyString())).thenReturn(null);

        // Act
        ApiResponse<Void> response = authService.login(loginDao, this.response);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid credentials", response.getMessage());
        verify(userService, times(1)).findByUsername(anyString());
    }

    @Test
    void refresh_WithValidToken_ShouldReturnSuccess() {
        // Arrange
        when(jwtService.verifyToken(anyString())).thenReturn(true);
        when(jwtService.getUserDetails(anyString())).thenReturn(user);
        when(userService.findByUsername(anyString())).thenReturn(user);
        when(jwtService.generateAccessTokenCookie(anyString())).thenReturn(null);

        // Act
        ApiResponse<Void> response = authService.refresh("valid_refresh_token", this.response);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Token refreshed", response.getMessage());
        verify(jwtService, times(1)).verifyToken(anyString());
        verify(jwtService, times(1)).getUserDetails(anyString());
        verify(userService, times(1)).findByUsername(anyString());
        verify(jwtService, times(1)).generateAccessTokenCookie(anyString());
    }

    @Test
    void refresh_WithInvalidToken_ShouldReturnFailure() {
        // Arrange
        when(jwtService.verifyToken(anyString())).thenReturn(false);

        // Act
        ApiResponse<Void> response = authService.refresh("invalid_refresh_token", this.response);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid refresh token", response.getMessage());
        verify(jwtService, times(1)).verifyToken(anyString());
    }

    @Test
    void logout_ShouldReturnSuccess() {
        // Arrange
        when(jwtService.generateLogoutAccessCookie()).thenReturn(null);
        when(jwtService.generateLogoutRefreshCookie()).thenReturn(null);

        // Act
        ApiResponse<Void> response = authService.logout(this.response);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Logout successful", response.getMessage());
        verify(jwtService, times(1)).generateLogoutAccessCookie();
        verify(jwtService, times(1)).generateLogoutRefreshCookie();
    }
}
