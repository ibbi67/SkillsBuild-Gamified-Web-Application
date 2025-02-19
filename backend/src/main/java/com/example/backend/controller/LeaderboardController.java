package com.example.backend.controller;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.LeaderboardEntry;
import com.example.backend.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getLeaderboard() {
        ApiResponse<List<LeaderboardEntry>> leaderboardResponse = leaderboardService.getLeaderboard();
        return ResponseEntity.status(leaderboardResponse.getStatus()).body(leaderboardResponse);
    }
}