package com.example.backend.enrollment.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnrollmentGetAllError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    ENROLLMENT_NOT_FOUND("Enrollment not found");

    private final String message;
}
