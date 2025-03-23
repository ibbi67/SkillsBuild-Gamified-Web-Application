package com.example.backend.auth.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthSignupError {
    USERNAME_CANNOT_BE_NULL_OR_EMPTY("Username cannot be null or empty"),
    PASSWORD_CANNOT_BE_NULL_OR_EMPTY("Password cannot be null or empty"),
    USERNAME_ALREADY_EXISTS("Username already exists"),
    STREAK_UPDATE_FAILED("Failed to update streak");

    private final String message;
}
