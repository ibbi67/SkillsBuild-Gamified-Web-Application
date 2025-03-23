package com.example.backend.leaderboard.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeaderboardGetAllError {
    LEADERBOARD_NOT_FOUND("Leaderboard not found");

    private final String message;
}
