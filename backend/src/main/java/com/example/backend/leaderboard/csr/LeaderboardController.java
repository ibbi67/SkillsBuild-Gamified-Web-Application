package com.example.backend.leaderboard.csr;

import com.example.backend.leaderboard.LeaderboardDTO;
import com.example.backend.leaderboard.error.LeaderboardGetAllError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@Tag(name = "Leaderboard Controller", description = "API for leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @Operation(summary = "Get all leaderboard entries")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LeaderboardDTO>>> getAll() {
        ServiceResult<List<LeaderboardDTO>, LeaderboardGetAllError> result = leaderboardService.getAll();
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        LeaderboardGetAllError error = result.getError();
        return switch (error) {
            case LEADERBOARD_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}