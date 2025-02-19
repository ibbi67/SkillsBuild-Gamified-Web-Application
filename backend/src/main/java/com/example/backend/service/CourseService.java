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
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository,
                         JwtService jwtService,
                         UserService userService,
                         EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.jwtService = jwtService;
        this.userService = userService;
        this.enrollmentRepository = enrollmentRepository;
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

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(user.getId());
        if (enrollments.isEmpty()) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "No enrollments found");

        List<Course> userCourses = enrollments.stream().map(Enrollment::getCourse).toList();
        int totalDifficulty = userCourses.stream().mapToInt(Course::getDifficulty).sum();
        double averageDifficulty = (double) totalDifficulty / userCourses.size();
        int minDifficulty = (int) Math.max(averageDifficulty - 1, 0);
        int maxDifficulty = (int) Math.min(averageDifficulty + 1, 5);

        List<Course> recommendedCourses = courseRepository.findCoursesByDifficultyRange(minDifficulty, maxDifficulty);
        recommendedCourses.removeIf(userCourses::contains);

        if (recommendedCourses.isEmpty()) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "No courses found");

        return ApiResponse.success("Courses are successfully recommended", recommendedCourses);
    }
}