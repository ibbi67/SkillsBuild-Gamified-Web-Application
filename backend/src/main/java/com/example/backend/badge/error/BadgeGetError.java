package com.example.backend.badge.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeGetError {
    BADGE_NOT_FOUND("Badge not found"),
    GET_BADGE_FAILED("Failed to retrieve badge");

    private final String message;
} 