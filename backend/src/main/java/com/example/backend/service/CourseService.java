package com.example.backend.service;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public ApiResponse<List<Course>> get() {
        List<Course> courses = courseRepository.findAll();
        return ApiResponse.success("Courses found", courses);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(int id) {
        return courseRepository.findById(id).orElse(null);
    }
}