package com.example.backend.goals.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GoalAddEnrollmentError {
    GOAL_NOT_FOUND("Goal not found"),
    ENROLLMENTS_NOT_FOUND("Enrollments not found");

    private final String message;
}
