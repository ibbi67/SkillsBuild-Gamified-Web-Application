package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.backend.domain.User;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private JwtService jwtService;

    private static final String TEST_USERNAME = "testuser" + LocalDateTime.now().hashCode();

    @Test
    void generateAccessTokenCookie_ShouldReturnValidCookie() {
        // Arrange + Act
        Cookie cookie = jwtService.generateAccessTokenCookie(TEST_USERNAME);

        // Assert
        assertNotNull(cookie, "Cookie should not be null");
        assertEquals("access_token", cookie.getName());
        assertTrue(cookie.getMaxAge() > 0, "Access token maxAge should be positive");
        assertTrue(cookie.isHttpOnly(), "Cookie should be HttpOnly");
        assertTrue(cookie.getSecure(), "Cookie should be secure");
        assertEquals("/", cookie.getPath(), "Cookie path should be /");
        assertEquals("localhost", cookie.getDomain(), "Cookie domain should be localhost");
        assertFalse(cookie.getValue().isBlank(), "Cookie value (the token) should not be blank");
    }

    @Test
    void generateRefreshTokenCookie_ShouldReturnValidCookie() {
        // Arrange + Act
        Cookie cookie = jwtService.generateRefreshTokenCookie(TEST_USERNAME);

        // Assert
        assertNotNull(cookie, "Cookie should not be null");
        assertEquals("refresh_token", cookie.getName());
        assertTrue(cookie.getMaxAge() > 3600, "Refresh token maxAge should be greater than 3600");
        assertTrue(cookie.isHttpOnly(), "Cookie should be HttpOnly");
        assertTrue(cookie.getSecure(), "Cookie should be secure");
        assertEquals("/", cookie.getPath(), "Cookie path should be /");
        assertEquals("localhost", cookie.getDomain(), "Cookie domain should be localhost");
        assertFalse(cookie.getValue().isBlank(), "Cookie value (the token) should not be blank");
    }

    @Test
    void verifyToken_ValidTokenShouldReturnTrue() {
        // Arrange
        Cookie accessCookie = jwtService.generateAccessTokenCookie(TEST_USERNAME);
        String token = accessCookie.getValue();

        // Act
        boolean result = jwtService.verifyToken(token);

        // Assert
        assertTrue(result, "verifyToken should return true for a valid token");
    }

    @Test
    void verifyToken_InvalidTokenShouldReturnFalse() {
        // Arrange
        String invalidToken = jwtService.generateAccessTokenCookie(TEST_USERNAME).getValue() + "invalidSuffix";

        // Act
        boolean result = jwtService.verifyToken(invalidToken);

        // Assert
        assertFalse(result, "verifyToken should return false for an invalid token");
    }

    @Test
    void verifyToken_NullOrBlankShouldReturnFalse() {
        // Arrange + Act + Assert
        assertFalse(jwtService.verifyToken(null), "verifyToken(null) should return false");
        assertFalse(jwtService.verifyToken(""), "verifyToken(\"\") should return false");
        assertFalse(jwtService.verifyToken("   "), "verifyToken(\"   \") should return false");
    }

    @Test
    void getUserDetails_ShouldReturnUserFromToken() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername(TEST_USERNAME);
        when(userService.findByUsername(TEST_USERNAME)).thenReturn(mockUser);
        Cookie accessCookie = jwtService.generateAccessTokenCookie(TEST_USERNAME);
        String token = accessCookie.getValue();

        // Act
        User extractedUser = jwtService.getUserDetails(token);

        // Assert
        assertNotNull(extractedUser, "Extracted user should not be null");
        assertEquals(TEST_USERNAME, extractedUser.getUsername(), "Usernames should match the token subject");
    }

    @Test
    void getUserDetails_InvalidTokenShouldThrowJwtExceptionOrReturnNull() {
        // Arrange
        String invalidToken = "invalid.token.value";

        // Act + Assert
        assertThrows(
            JwtException.class,
            () -> jwtService.getUserDetails(invalidToken),
            "Should throw a JwtException for a malformed token"
        );
    }

    @Test
    void generateLogoutCookies_ShouldHaveMaxAgeZero() {
        // Arrange + Act
        Cookie logoutAccessCookie = jwtService.generateLogoutAccessCookie();
        Cookie logoutRefreshCookie = jwtService.generateLogoutRefreshCookie();

        // Assert
        assertEquals("access_token", logoutAccessCookie.getName());
        assertEquals(0, logoutAccessCookie.getMaxAge());
        assertEquals("refresh_token", logoutRefreshCookie.getName());
        assertEquals(0, logoutRefreshCookie.getMaxAge());
    }

    @Test
    void secretKey_ShouldNotBeNull() {
        // Arrange + Act
        SecretKey secretKey = jwtService.SECRET_KEY;

        // Assert
        assertNotNull(secretKey, "SECRET_KEY should not be null");
    }

    @Test
    void parseValidToken_WithTamperedSignature_ShouldFail() {
        // Arrange
        String token = jwtService.generateAccessTokenCookie(TEST_USERNAME).getValue();
        String tampered = token.substring(0, token.length() - 1);

        // Act
        boolean result = jwtService.verifyToken(tampered);

        // Assert
        assertFalse(result, "verifyToken should return false if token signature is invalid");
    }
}
