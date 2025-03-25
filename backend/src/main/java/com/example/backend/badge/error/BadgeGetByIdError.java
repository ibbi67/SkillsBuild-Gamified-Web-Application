package com.example.backend.badge.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeGetByIdError {
    INVALID_ID("Invalid badge ID"),
    BADGE_NOT_FOUND("Badge not found");
    
    private final String message;
}
