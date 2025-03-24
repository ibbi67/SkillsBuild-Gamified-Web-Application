package com.example.backend.comment.error;

public enum CommentGetByCourseError {
    COURSE_NOT_FOUND("Course not found"),
    GET_COMMENTS_FAILED("Failed to get comments");

    private final String message;

    CommentGetByCourseError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}



