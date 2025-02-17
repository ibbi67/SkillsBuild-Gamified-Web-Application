package com.example.backend.controller;

import com.example.backend.domain.Course;
import com.example.backend.repository.CourseRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    
    @GetMapping("/api/courses")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();  
    }
}
