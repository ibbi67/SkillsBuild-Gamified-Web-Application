package com.example.backend.course.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseGetTrendingError {
    GET_TRENDING_COURSES_FAILED("Failed to get trending courses");

    private final String message;
}