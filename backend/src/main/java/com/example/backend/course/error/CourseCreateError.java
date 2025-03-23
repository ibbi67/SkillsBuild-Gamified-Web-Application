package com.example.backend.course.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseCreateError {
    COURSE_CREATION_FAILED("Failed to create course");

    private final String message;
}
