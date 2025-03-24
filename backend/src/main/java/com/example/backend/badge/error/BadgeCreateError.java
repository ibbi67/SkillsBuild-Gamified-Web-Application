package com.example.backend.badge.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeCreateError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    UNAUTHORIZED("Only administrators can create badges"),
    BADGE_CREATION_FAILED("Failed to create badge");

    private final String message;
} 