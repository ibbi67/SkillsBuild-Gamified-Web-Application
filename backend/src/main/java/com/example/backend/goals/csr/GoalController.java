package com.example.backend.goals.csr;

import com.example.backend.goals.AddEnrollmentDTO;
import com.example.backend.goals.Goal;
import com.example.backend.goals.GoalDTO;
import com.example.backend.goals.error.*;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // Create a new goal
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createGoal(@CookieValue("accessToken") String accessToken, @RequestBody GoalDTO goalDTO) {
        ServiceResult<Void, GoalCreateError> result = goalService.createGoal(accessToken, goalDTO);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        }
        GoalCreateError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.UNAUTHORIZED);
            case FAILED_TO_CREATE_GOAL ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    // Delete a goal
    @DeleteMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(@CookieValue("accessToken") String accessToken, @PathVariable Long goalId) {
        ServiceResult<Void, GoalDeleteError> result = goalService.deleteGoal(accessToken, goalId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        }
        GoalDeleteError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.UNAUTHORIZED);
            case GOAL_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.NOT_FOUND);
            case PERMISSION_DENIED ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.FORBIDDEN);
        };
    }

    // Add courses to a goal
    @PostMapping("/{goalId}")
    public ResponseEntity<ApiResponse<Goal>> addEnrollmentToGoal(@PathVariable Long goalId, @RequestBody AddEnrollmentDTO addEnrollmentDTO) {
        ServiceResult<Goal, GoalAddEnrollmentError> result = goalService.addEnrollmentToGoal(goalId, addEnrollmentDTO);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }
        GoalAddEnrollmentError error = result.getError();
        return switch (error) {
            case GOAL_NOT_FOUND, ENROLLMENTS_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    // update enrollment completion status
    @PutMapping("/{goalId}/enrollments/{enrollmentId}")
    public ResponseEntity<ApiResponse<Goal>> updateEnrollmentCompletionStatus(@CookieValue("accessToken") String accessToken, @PathVariable Long goalId, @PathVariable Integer enrollmentId) {
        ServiceResult<Goal, GoalUpdateEnrollmentCompletionStatusError> result = goalService.updateEnrollmentCompletionStatus(accessToken, goalId, enrollmentId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }
        GoalUpdateEnrollmentCompletionStatusError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.UNAUTHORIZED);
            case GOAL_NOT_FOUND, ENROLLMENTS_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            case PERMISSION_DENIED ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.FORBIDDEN);
        };
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Goal>>> getGoals(@CookieValue("accessToken") String accessToken) {
        ServiceResult<List<Goal>, GoalGetError> result = goalService.getGoals(accessToken);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }
        GoalGetError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.UNAUTHORIZED);
        };
    }


}