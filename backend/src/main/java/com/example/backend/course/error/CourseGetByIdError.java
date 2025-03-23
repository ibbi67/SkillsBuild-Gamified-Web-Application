package com.example.backend.course.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseGetByIdError {
    GET_COURSE_BY_ID_FAILED("Failed to retrieve course by ID"),
    COURSE_NOT_FOUND("Course not found");

    private final String message;
}
