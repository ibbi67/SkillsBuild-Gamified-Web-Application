package com.example.backend.course.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseViewError {
    COURSE_NOT_FOUND("Course not found"),
    VIEW_INCREMENT_FAILED("Failed to increment view count"),
    INVALID_ID("Invalid id provided");

    private final String message;
}