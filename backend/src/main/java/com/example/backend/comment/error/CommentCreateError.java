package com.example.backend.comment.error;

public enum CommentCreateError {
    UNAUTHORIZED("Unauthorized"),
    COURSE_NOT_FOUND("Course not found"),
    COMMENT_CREATION_FAILED("Failed to create comment");

    private final String message;

    CommentCreateError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
