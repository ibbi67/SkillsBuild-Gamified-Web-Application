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
    INVALID_REQUEST("Invalid request"),
    CONTENT_TOO_SHORT("Comment is too short (minimum 2 characters)"),
    CONTENT_TOO_LONG("Comment is too long (maximum 1000 characters)"),
    INAPPROPRIATE_CONTENT("Comment contains inappropriate content"),
    RATE_LIMIT_EXCEEDED("You are posting comments too frequently. Please try again later.");

    private final String message;
}