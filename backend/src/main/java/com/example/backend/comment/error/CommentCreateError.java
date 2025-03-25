package com.example.backend.comment.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum CommentCreateError {
    UNAUTHORIZED("Unauthorized"),
    COURSE_NOT_FOUND("Course not found"),
    COMMENT_CREATION_FAILED("Failed to create comment"),
    EMPTY_CONTENT("Comment content cannot be empty"),
    INVALID_COURSE_ID("Invalid course ID"),
    INVALID_REQUEST("Invalid request");

    private final String message;
}