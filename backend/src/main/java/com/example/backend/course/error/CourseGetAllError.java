package com.example.backend.course.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseGetAllError {
    GET_ALL_COURSES_FAILED("Failed to retrieve all courses");

    private final String message;
}
