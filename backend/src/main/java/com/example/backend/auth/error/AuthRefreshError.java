package com.example.backend.auth.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthRefreshError {
    INVALID_REFRESH_TOKEN("Invalid refresh token"),
    STREAK_UPDATE_FAILED("Failed to update streak");

    private final String message;
}
