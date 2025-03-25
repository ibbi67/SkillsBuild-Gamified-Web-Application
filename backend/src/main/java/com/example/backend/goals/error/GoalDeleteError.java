package com.example.backend.goals.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalDeleteError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    GOAL_NOT_FOUND("Goal not found"),
    PERMISSION_DENIED("Permission denied");

    private final String message;
}
