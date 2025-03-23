package com.example.backend.favourite.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FavouriteCreateError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    COURSE_ALREADY_FAVORITE("Course is already in favorites"),
    COURSE_NOT_FOUND("Course not found");

    private final String message;
}
