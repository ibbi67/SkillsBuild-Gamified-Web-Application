package com.example.backend.controller;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable int id) {
        return courseService.getCourseById(id);
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<Course>> getRecommendedCourses(@CookieValue(value = "access_token", required = false) String accessToken) {
        ApiResponse<List<Course>> getRecommendedCoursesResponse = courseService.getRecommendedCourses(accessToken);
        return ResponseEntity.status(getRecommendedCoursesResponse.getStatus()).body(getRecommendedCoursesResponse.getData());
    }

}