package com.example.backend.favourite.csr;

import com.example.backend.course.Course;
import com.example.backend.favourite.error.FavouriteCreateError;
import com.example.backend.favourite.error.FavouriteGetAllError;
import com.example.backend.favourite.error.FavouriteRemoveError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/favourites")
@Tag(name = "Favourite Controller", description = "API for favourites")
public class FavouriteController {
    private final FavouriteService favouriteService;

    public FavouriteController(FavouriteService favouriteService) {
        this.favouriteService = favouriteService;
    }

    @Operation(summary = "Get all favourite courses")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getAll(@CookieValue(name = "accessToken") String accessToken) {
        ServiceResult<List<Course>, FavouriteGetAllError> result = favouriteService.getAll(accessToken);
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getData()));
        }

        return switch (result.getError()) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.UNAUTHORIZED);
        };
    }

    @Operation(summary = "Add a course to favourites")
    @PostMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Void>> create(@CookieValue(name = "accessToken") String accessToken, @PathVariable Integer courseId) {
        ServiceResult<Void, FavouriteCreateError> result = favouriteService.create(accessToken, courseId);
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        return switch (result.getError()) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.UNAUTHORIZED);
            case COURSE_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.NOT_FOUND);
            case COURSE_ALREADY_FAVORITE ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.CONFLICT);
        };
    }

    @Operation(summary = "Remove a course from favourites")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Void>> remove(@CookieValue(name = "accessToken") String accessToken, @PathVariable Integer courseId) {
        ServiceResult<Void, FavouriteRemoveError> result = favouriteService.remove(accessToken, courseId);
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }

        return switch (result.getError()) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.UNAUTHORIZED);
            case COURSE_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.NOT_FOUND);
            case COURSE_NOT_FAVORITE ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.CONFLICT);
        };
    }
}
