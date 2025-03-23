package com.example.backend.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LeaderboardDTO {
    private String username;
    private Integer score;
}
