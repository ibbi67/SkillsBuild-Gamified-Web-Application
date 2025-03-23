package com.example.backend.auth.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthUpdateStreakError {
    STREAK_UPDATE_FAILED("Failed to update streak");

    private final String message;
}
