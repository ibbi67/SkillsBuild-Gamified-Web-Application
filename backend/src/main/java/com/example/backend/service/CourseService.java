package com.example.backend.service;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.domain.Enrollment;
import com.example.backend.domain.User;
import com.example.backend.repository.CourseRepository;
import com.example.backend.repository.EnrollmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final JwtService jwtService;
    private final UserService userService;

    public CourseService(CourseRepository courseRepository, JwtService jwtService, UserService userService) {
        this.courseRepository = courseRepository;
        this.jwtService = jwtService;
        this.userService = userService;
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

    public ApiResponse<List<Course>> getRecommendedCourses(String accessToken) {
        if (accessToken == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid access token");
        if (!jwtService.verifyToken(accessToken))
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid access token");

        String username = jwtService.getUserDetails(accessToken).getUsername();
        User user = userService.findByUsername(username);
        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid access token");

        List<Course> favoriteCourses = user.getFavouriteCourses();
        if (favoriteCourses.isEmpty())
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "No favorite courses found");

        int totalDifficulty = favoriteCourses.stream().mapToInt(Course::getDifficulty).sum();
        double averageDifficulty = (double) totalDifficulty / favoriteCourses.size();
        int minDifficulty = (int) Math.max(averageDifficulty - 1, 0);
        int maxDifficulty = (int) Math.min(averageDifficulty + 1, 5);

        List<Course> recommendedCourses = courseRepository.findCoursesByDifficultyRange(minDifficulty, maxDifficulty);
        recommendedCourses.removeIf(favoriteCourses::contains);

        if (recommendedCourses.isEmpty()) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "No courses found");

        return ApiResponse.success("Courses are successfully recommended", recommendedCourses);
    }
}