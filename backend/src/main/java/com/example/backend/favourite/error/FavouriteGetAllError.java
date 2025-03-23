package com.example.backend.favourite.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FavouriteGetAllError {
    INVALID_ACCESS_TOKEN("Invalid access token");

    private final String message;
}
