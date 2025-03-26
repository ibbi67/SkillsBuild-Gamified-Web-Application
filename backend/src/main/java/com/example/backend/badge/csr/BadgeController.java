package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.badge.error.BadgeGetByIdError;
import com.example.backend.badge.error.BadgeGetByUserError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badges")
@Tag(name = "Badge Controller", description = "API for managing badges")
public class BadgeController {

    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @GetMapping
    @Operation(summary = "Get all badges", description = "Retrieves a list of all available badges.")
    public ResponseEntity<ApiResponse<List<Badge>>> getAllBadges() {
        ServiceResult<List<Badge>, Void> result = badgeService.getAllBadges();
        return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get badge by ID", description = "Retrieves a badge by its ID.")
    public ResponseEntity<ApiResponse<Badge>> getBadgeById(@PathVariable Integer id) {
        ServiceResult<Badge, BadgeGetByIdError> result = badgeService.getBadgeById( id);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        BadgeGetByIdError error = result.getError();
        return switch (error) {
            case INVALID_ID -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case BADGE_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
        };
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get badges by user ID", description = "Retrieves a list of badges for a specific user.")
    public ResponseEntity<ApiResponse<List<Badge>>> getUserBadges(@CookieValue("accessToken") String accessToken) {
        ServiceResult<List<Badge>, BadgeGetByUserError> result = badgeService.getUserBadges(accessToken);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        BadgeGetByUserError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
        };
    }
}
