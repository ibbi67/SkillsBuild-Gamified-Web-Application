package com.example.backend.friend.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendRemoveError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    PERSON_NOT_FOUND("User not found"),
    NOT_FRIENDS("You are not friends with this user"),
    FRIEND_REMOVE_FAILED("Failed to remove friend");

    private final String message;
}
