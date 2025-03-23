package com.example.backend.course.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CourseGetRecommendError {
    INVALID_ACCESS_TOKEN("Invalid access token");

    private final String message;
}
