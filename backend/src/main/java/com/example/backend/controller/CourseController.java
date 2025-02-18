package com.example.backend.controller;

import com.example.backend.domain.Course;
import com.example.backend.repository.CourseRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
    
    @GetMapping("")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();  
    }
}
