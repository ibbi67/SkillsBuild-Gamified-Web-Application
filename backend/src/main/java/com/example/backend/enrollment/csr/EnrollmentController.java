package com.example.backend.enrollment.csr;

import com.example.backend.enrollment.Enrollment;
import com.example.backend.enrollment.error.EnrollmentCreateError;
import com.example.backend.enrollment.error.EnrollmentGetAllError;
import com.example.backend.enrollment.error.EnrollmentGetByIdError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/enrollments")
@Tag(name = "Enrollment Controller", description = "API for enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @Operation(summary = "Get all enrollments")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Enrollment>>> getAll(@CookieValue(name = "accessToken") String accessToken) {
        ServiceResult<List<Enrollment>, EnrollmentGetAllError> result = enrollmentService.getAll(accessToken);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }
        EnrollmentGetAllError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.UNAUTHORIZED);
            case ENROLLMENT_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.NOT_FOUND);
        };
    }

    @Operation(summary = "Get enrollment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Enrollment>> getById(@PathVariable Integer id) {
        ServiceResult<Enrollment, EnrollmentGetByIdError> result = enrollmentService.getById(id);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }
        EnrollmentGetByIdError error = result.getError();
        return switch (error) {
            case INVALID_ID ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.BAD_REQUEST);
            case ENROLLMENT_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.NOT_FOUND);
        };
    }

    @Operation(summary = "Create a new enrollment")
    @PostMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Enrollment>> create(@CookieValue(name = "accessToken") String accessToken, @PathVariable Integer courseId) {
        ServiceResult<Enrollment, EnrollmentCreateError> result = enrollmentService.create(accessToken, courseId);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.CREATED);
        }
        EnrollmentCreateError error = result.getError();
        return switch (error) {
            case INVALID_COURSE_ID ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.BAD_REQUEST);
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.UNAUTHORIZED);
            case COURSE_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.NOT_FOUND);
            case ENROLLMENT_CREATION_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(result.getError().getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}
