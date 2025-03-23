package com.example.backend.favourite.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FavouriteRemoveError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    COURSE_NOT_FOUND("Course not found"),
    COURSE_NOT_FAVORITE("Course is not favorite");

    private final String message;
}
