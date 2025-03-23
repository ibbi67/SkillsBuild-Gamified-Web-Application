package com.example.backend.streak.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StreakGetError {
    INVALID_ACCESS_TOKEN("Invalid access token");

    private final String message;
}
