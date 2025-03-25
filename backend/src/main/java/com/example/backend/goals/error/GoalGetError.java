package com.example.backend.goals.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalGetError {
    INVALID_ACCESS_TOKEN("Invalid access token");

    private final String message;
}
