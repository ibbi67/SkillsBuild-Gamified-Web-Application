package com.example.backend.controller;

import com.example.backend.domain.Course;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final List<Course> courses = Arrays.asList(
            new Course(1, "Introduction to AI", "Learn the basics of AI", "AI", "Beginner"),
            new Course(2, "Cybersecurity Fundamentals", "Protect systems from attacks", "Cybersecurity", "Intermediate"),
            new Course(3, "Cloud Computing Basics", "Understand cloud technologies", "Cloud", "Beginner"),
            new Course(4, "Java Programming", "Master Java development", "Programming", "Advanced")
    );

    @GetMapping
    public List<Course> getAllCourses() {
        return courses;
    }

    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable int id) {
        return courses.stream()
                .filter(course -> course.getId() == id)
                .findFirst()
                .orElse(null);
    }
}