package com.example.backend.profile.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProfileUpdateError {
    INVALID_ACCESS_TOKEN("Invalid access token"),
    PROFILE_UPDATE_FAILED("Profile update failed");
    
    private final String message;
}
