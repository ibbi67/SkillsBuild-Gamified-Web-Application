package com.example.backend.controller;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }
    
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Course>>> get() {
        ApiResponse<List<Course>> getResponse = courseService.get();
        return ResponseEntity.status(getResponse.getStatus()).body(getResponse);
    }
}
