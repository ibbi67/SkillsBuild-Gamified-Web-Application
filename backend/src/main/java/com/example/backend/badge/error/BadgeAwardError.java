package com.example.backend.badge.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeAwardError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    UNAUTHORIZED("Only administrators can award badges"),
    USER_NOT_FOUND("User not found"),
    BADGE_NOT_FOUND("Badge not found"),
    BADGE_AWARD_FAILED("Failed to award badge");

    private final String message;
} 