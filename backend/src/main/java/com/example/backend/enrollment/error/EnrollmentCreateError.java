package com.example.backend.enrollment.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnrollmentCreateError {
    INVALID_COURSE_ID("Invalid course ID"),
    INVALID_ACCESS_TOKEN("Invalid access token"),
    COURSE_NOT_FOUND("Course not found"),
    ENROLLMENT_CREATION_FAILED("Failed to create enrollment");

    private final String message;
}
