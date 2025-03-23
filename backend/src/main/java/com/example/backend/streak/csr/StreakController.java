package com.example.backend.streak.csr;

import com.example.backend.streak.error.StreakGetError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/streak")
@Tag(name = "Streak Controller", description = "API for streaks")
public class StreakController {

    private final StreakService streakService;

    public StreakController(StreakService streakService) {
        this.streakService = streakService;
    }

    @Operation(summary = "Get current streak")
    @GetMapping
    public ResponseEntity<ApiResponse<Integer>> getStreak(@CookieValue(name = "accessToken") String accessToken) {
        ServiceResult<Integer, StreakGetError> result = streakService.getStreak(accessToken);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        StreakGetError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
        };
    }
}
