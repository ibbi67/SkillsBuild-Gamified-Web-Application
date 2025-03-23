package com.example.backend.auth.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthLoginError {
    USERNAME_CANNOT_BE_NULL_OR_EMPTY("Username cannot be null or empty"),
    PASSWORD_CANNOT_BE_NULL_OR_EMPTY("Password cannot be null or empty"),
    INVALID_USERNAME_OR_PASSWORD("Invalid username or password"),
    STREAK_UPDATE_FAILED("Failed to update streak");

    private final String message;
}
