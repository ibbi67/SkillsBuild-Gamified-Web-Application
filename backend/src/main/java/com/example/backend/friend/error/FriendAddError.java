package com.example.backend.friend.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendAddError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    PERSON_NOT_FOUND("User not found"),
    ALREADY_FRIENDS("Already friends"),
    CANNOT_ADD_SELF("Cannot add yourself as a friend"),
    FRIEND_ADD_FAILED("Failed to add friend");

    private final String message;
}
