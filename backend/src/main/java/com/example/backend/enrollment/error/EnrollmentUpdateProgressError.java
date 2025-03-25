package com.example.backend.enrollment.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnrollmentUpdateProgressError {
    INVALID_ENROLLMENT_ID("Invalid enrollment ID"),
    INVALID_TIME_SPENT("Invalid time spent"),
    ENROLLMENT_NOT_FOUND("Enrollment not found"),
    ENROLLMENT_UPDATE_FAILED("Failed to update enrollment"),;
    
    private final String message;
}
