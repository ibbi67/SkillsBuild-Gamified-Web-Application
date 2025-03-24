package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.badge.BadgeDTO;
import com.example.backend.badge.error.BadgeCreateError;
import com.example.backend.badge.error.BadgeGetError;
import com.example.backend.badge.error.BadgeAwardError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import com.example.backend.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/badges")
public class BadgeController {

    private final BadgeService badgeService;
    private final JWT jwt;

    @Autowired
    public BadgeController(BadgeService badgeService, JWT jwt) {
        this.badgeService = badgeService;
        this.jwt = jwt;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Badge>>> getAllBadges() {
        ServiceResult<List<Badge>, BadgeGetError> result = badgeService.getAllBadges();
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        BadgeGetError error = result.getError();
        return switch (error) {
            case GET_BADGE_FAILED -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            case BADGE_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
        };
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Badge>> getBadgeById(@PathVariable Integer id) {
        ServiceResult<Badge, BadgeGetError> result = badgeService.getBadgeById(id);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        BadgeGetError error = result.getError();
        return switch (error) {
            case GET_BADGE_FAILED -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case BADGE_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
        };
    }

    @GetMapping("/type/{criteriaType}")
    public ResponseEntity<ApiResponse<List<Badge>>> getBadgesByCriteriaType(@PathVariable String criteriaType) {
        ServiceResult<List<Badge>, BadgeGetError> result = badgeService.getBadgesByCriteriaType(criteriaType);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        BadgeGetError error = result.getError();
        return switch (error) {
            case GET_BADGE_FAILED -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case BADGE_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
        };
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Set<Badge>>> getUserBadges(@PathVariable Integer userId) {
        ServiceResult<Set<Badge>, BadgeGetError> result = badgeService.getUserBadges(userId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        BadgeGetError error = result.getError();
        return switch (error) {
            case GET_BADGE_FAILED -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case BADGE_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
        };
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Badge>> createBadge(@RequestBody BadgeDTO badgeDTO, @CookieValue("accessToken") String accessToken) {
        if (!isValidToken(accessToken)) {
            return new ResponseEntity<>(ApiResponse.failed(BadgeCreateError.UNAUTHORIZED.getMessage()), HttpStatus.FORBIDDEN);
        }

        ServiceResult<Badge, BadgeCreateError> result = badgeService.createBadge(badgeDTO);

        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.CREATED);
        }

        BadgeCreateError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
            case UNAUTHORIZED -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.FORBIDDEN);
            case BADGE_CREATION_FAILED -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @PostMapping("/award/{userId}/{badgeId}")
    public ResponseEntity<ApiResponse<String>> awardBadgeToUser(
            @PathVariable Integer userId,
            @PathVariable Integer badgeId,
            @CookieValue("accessToken") String accessToken
    ) {
        if (!isValidToken(accessToken)) {
            return new ResponseEntity<>(ApiResponse.failed(BadgeAwardError.UNAUTHORIZED.getMessage()), HttpStatus.FORBIDDEN);
        }

        ServiceResult<Void, BadgeAwardError> result = badgeService.awardBadgeToUser(userId, badgeId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success("Badge awarded successfully"), HttpStatus.OK);
        }

        BadgeAwardError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
            case UNAUTHORIZED -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.FORBIDDEN);
            case USER_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
            case BADGE_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
            case BADGE_AWARD_FAILED -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    private boolean isValidToken(String accessToken) {
        return jwt.getPersonFromToken(accessToken).isPresent();
    }
}