package com.example.backend.comment.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum CommentGetByCourseError {
    COURSE_NOT_FOUND("Course not found"),
    GET_COMMENTS_FAILED("Failed to get comments"),
    INVALID_COURSE_ID("Invalid course ID");

    private final String message;
}