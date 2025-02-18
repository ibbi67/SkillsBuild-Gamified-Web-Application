package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.domain.Enrollment;
import com.example.backend.domain.User;
import com.example.backend.repository.CourseRepository;
import com.example.backend.repository.EnrollmentRepository;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private CourseService courseService;

    private List<Course> testCourses;
    private User testUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        testCourses = Arrays.asList(
            new Course(
                "Course 1",
                "Description 1",
                "link1",
                Duration.ofHours(1),
                1
            ),
            new Course(
                "Course 2",
                "Description 2",
                "link2",
                Duration.ofHours(2),
                2
            ),
            new Course(
                "Course 3",
                "Description 3",
                "link3",
                Duration.ofHours(3),
                3
            )
        );

        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");

        validToken = "valid.jwt.token";
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(testCourses);

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertEquals(testCourses.size(), result.size());
        assertEquals(testCourses, result);
    }

    @Test
    void getCourseById_WithValidId_ShouldReturnCourse() {
        // Arrange
        Course course = testCourses.get(0);
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        // Act
        Course result = courseService.getCourseById(1);

        // Assert
        assertNotNull(result);
        assertEquals(course, result);
    }

    @Test
    void getRecommendedCourses_WithValidTokenAndEnrollments_ShouldReturnRecommendedCourses() {
        // Arrange
        when(jwtService.verifyToken(validToken)).thenReturn(true);
        when(jwtService.getUserDetails(validToken)).thenReturn(testUser);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(
            testUser
        );

        Course enrolledCourse = new Course(
            "Enrolled Course",
            "Description",
            "link",
            Duration.ofHours(2),
            2
        );
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(enrolledCourse);
        enrollment.setUser(testUser);

        when(enrollmentRepository.findByUserId(testUser.getId())).thenReturn(
            Collections.singletonList(enrollment)
        );

        List<Course> recommendedCourses = Arrays.asList(
            new Course(
                "Recommended 1",
                "Desc 1",
                "link1",
                Duration.ofHours(1),
                2
            ),
            new Course(
                "Recommended 2",
                "Desc 2",
                "link2",
                Duration.ofHours(2),
                3
            )
        );

        when(
            courseRepository.findCoursesByDifficultyRange(anyInt(), anyInt())
        ).thenReturn(recommendedCourses);

        // Act
        ApiResponse<List<Course>> result = courseService.getRecommendedCourses(
            validToken
        );

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatus());
        assertNotNull(result.getData());
        assertEquals(recommendedCourses.size(), result.getData().size());
    }

    @Test
    void getRecommendedCourses_WithInvalidToken_ShouldReturnError() {
        // Arrange
        String invalidToken = "invalid.token";
        when(jwtService.verifyToken(invalidToken)).thenReturn(false);

        // Act
        ApiResponse<List<Course>> result = courseService.getRecommendedCourses(
            invalidToken
        );

        // Assert
        assertNotNull(result);
        assertEquals(400, result.getStatus());
        assertEquals("Invalid access token", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void getRecommendedCourses_WithNoEnrollments_ShouldReturnError() {
        // Arrange
        when(jwtService.verifyToken(validToken)).thenReturn(true);
        when(jwtService.getUserDetails(validToken)).thenReturn(testUser);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(
            testUser
        );
        when(enrollmentRepository.findByUserId(testUser.getId())).thenReturn(
            Collections.emptyList()
        );

        // Act
        ApiResponse<List<Course>> result = courseService.getRecommendedCourses(
            validToken
        );

        // Assert
        assertNotNull(result);
        assertEquals(400, result.getStatus());
        assertEquals("No enrollments found", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void getRecommendedCourses_WithNoRecommendedCourses_ShouldReturnError() {
        // Arrange
        when(jwtService.verifyToken(validToken)).thenReturn(true);
        when(jwtService.getUserDetails(validToken)).thenReturn(testUser);
        when(userService.findByUsername(testUser.getUsername())).thenReturn(
            testUser
        );

        Course enrolledCourse = new Course(
            "Enrolled Course",
            "Description",
            "link",
            Duration.ofHours(2),
            2
        );
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(enrolledCourse);
        enrollment.setUser(testUser);

        when(enrollmentRepository.findByUserId(testUser.getId())).thenReturn(
            Collections.singletonList(enrollment)
        );

        when(
            courseRepository.findCoursesByDifficultyRange(anyInt(), anyInt())
        ).thenReturn(Collections.emptyList());

        // Act
        ApiResponse<List<Course>> result = courseService.getRecommendedCourses(
            validToken
        );

        // Assert
        assertNotNull(result);
        assertEquals(400, result.getStatus());
        assertEquals("No courses found", result.getMessage());
        assertNull(result.getData());
    }
}
