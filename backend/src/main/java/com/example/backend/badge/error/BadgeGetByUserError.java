package com.example.backend.badge.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BadgeGetByUserError {
    INVALID_ACCESS_TOKEN("Invalid access token");

    private final String message;
}
