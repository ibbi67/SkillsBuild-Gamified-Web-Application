package com.example.backend.course.csr;

import com.example.backend.course.Course;
import com.example.backend.course.CourseDTO;
import com.example.backend.course.error.CourseCreateError;
import com.example.backend.course.error.CourseGetAllError;
import com.example.backend.course.error.CourseGetByIdError;
import com.example.backend.course.error.CourseGetRecommendError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.course.error.CourseGetTrendingError;
import com.example.backend.course.error.CourseViewError;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/courses")
@Tag(name = "Course Controller", description = "API for courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Get all courses")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getAll() {
        ServiceResult<List<Course>, CourseGetAllError> result = courseService.getAll();
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        CourseGetAllError error = result.getError();
        return switch (error) {
            case GET_ALL_COURSES_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @Operation(summary = "Get trending courses")
    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<Course>>> getTrendingCourses() {
        ServiceResult<List<Course>, CourseGetTrendingError> result = courseService.getTrendingCourses();
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        CourseGetTrendingError error = result.getError();
        return switch (error) {
            case GET_TRENDING_COURSES_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @Operation(summary = "Increment course view count")
    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> incrementCourseView(@PathVariable Integer id) {
        ServiceResult<Void, CourseViewError> result = courseService.incrementCourseViews(id);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        }

        CourseViewError error = result.getError();
        return switch (error) {
            case COURSE_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
            case VIEW_INCREMENT_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @Operation(summary = "Get course by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getById(@PathVariable Integer id) {
        ServiceResult<Course, CourseGetByIdError> result = courseService.getById(id);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        CourseGetByIdError error = result.getError();
        return switch (error) {
            case GET_COURSE_BY_ID_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case COURSE_NOT_FOUND -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
        };
    }

    @Operation(summary = "Create a new course")
    @PostMapping
    public ResponseEntity<ApiResponse<Course>> create(@RequestBody CourseDTO courseDTO) {
        ServiceResult<Course, CourseCreateError> result = courseService.create(courseDTO);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.CREATED);
        }

        CourseCreateError error = result.getError();
        return switch (error) {
            case COURSE_CREATION_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @Operation(summary = "Get recommended courses")
    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<List<Course>>> getRecommendedCourses(@CookieValue("accessToken") String accessToken) {
        ServiceResult<List<Course>, CourseGetRecommendError> result = courseService.getRecommendedCourses(accessToken);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        CourseGetRecommendError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
        };
    }
}
