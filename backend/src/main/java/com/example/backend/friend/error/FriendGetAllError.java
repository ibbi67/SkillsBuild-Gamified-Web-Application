package com.example.backend.friend.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FriendGetAllError {
    INVALID_ACCESS_TOKEN("Invalid access token");

    private final String message;
}