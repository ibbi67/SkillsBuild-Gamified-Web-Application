package com.example.backend.auth.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthLogoutError {
    INVALID_REFRESH_TOKEN("Invalid refresh token");

    private final String message;
}