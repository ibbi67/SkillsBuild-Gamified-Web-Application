package com.example.backend.enrollment.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnrollmentGetByIdError {
    INVALID_ID("Invalid ID provided"),
    ENROLLMENT_NOT_FOUND("Enrollment not found");

    private final String message;
}
