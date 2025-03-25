package com.example.backend.goals.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalCreateError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    FAILED_TO_CREATE_GOAL("Failed to create goal");

    private final String message;
}
