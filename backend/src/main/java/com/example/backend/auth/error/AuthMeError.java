package com.example.backend.auth.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthMeError {
    INVALID_ACCESS_TOKEN("Invalid access token");

    private final String message;
}
