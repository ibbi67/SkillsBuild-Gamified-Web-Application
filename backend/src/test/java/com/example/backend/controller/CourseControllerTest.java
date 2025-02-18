package com.example.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.domain.User;
import com.example.backend.service.CourseService;
import com.example.backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@Import({ CourseService.class, JwtService.class })
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Course> testCourses;

    @BeforeEach
    void setUp() {
        // Create courses with IDs
        Course course1 = new Course(
            "Course 1",
            "Description 1",
            "link1",
            Duration.ofHours(1),
            1
        );
        course1.setId(1);
        Course course2 = new Course(
            "Course 2",
            "Description 2",
            "link2",
            Duration.ofHours(2),
            2
        );
        course2.setId(2);

        testCourses = Arrays.asList(course1, course2);

        User testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() throws Exception {
        // Arrange
        when(courseService.getAllCourses()).thenReturn(testCourses);

        // Act
        MvcResult result = mockMvc
            .perform(get("/courses").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        Course[] returnedCourses = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            Course[].class
        );

        assertEquals(testCourses.size(), returnedCourses.length);
        assertEquals(
            testCourses.get(0).getTitle(),
            returnedCourses[0].getTitle()
        );
        assertEquals(
            testCourses.get(1).getTitle(),
            returnedCourses[1].getTitle()
        );
    }

    @Test
    void getCourseById_WithValidId_ShouldReturnCourse() throws Exception {
        // Arrange
        Course course = testCourses.getFirst();
        when(courseService.getCourseById(1)).thenReturn(course);

        // Act
        MvcResult result = mockMvc
            .perform(get("/courses/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        Course returnedCourse = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            Course.class
        );

        assertEquals(course.getTitle(), returnedCourse.getTitle());
        assertEquals(course.getDescription(), returnedCourse.getDescription());
    }

    @Test
    void getRecommendedCourses_WithValidToken_ShouldReturnRecommendedCourses()
        throws Exception {
        // Arrange
        String validToken = "valid.jwt.token";
        ApiResponse<List<Course>> apiResponse = ApiResponse.success(
            "Courses found",
            testCourses
        );
        when(courseService.getRecommendedCourses(validToken)).thenReturn(
            apiResponse
        );

        // Act
        MvcResult result = mockMvc
            .perform(
                get("/courses/recommend")
                    .cookie(
                        new jakarta.servlet.http.Cookie(
                            "access_token",
                            validToken
                        )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn();

        // Assert
        List<Course> returnedCourses = Arrays.asList(
            objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Course[].class
            )
        );

        assertEquals(testCourses.size(), returnedCourses.size());
        assertEquals(
            testCourses.getFirst().getTitle(),
            returnedCourses.getFirst().getTitle()
        );
    }

    @Test
    void getRecommendedCourses_WithInvalidToken_ShouldReturnError()
        throws Exception {
        // Arrange
        String invalidToken = "invalid.token";
        ApiResponse<List<Course>> apiResponse = ApiResponse.failed(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid access token"
        );
        when(courseService.getRecommendedCourses(invalidToken)).thenReturn(
            apiResponse
        );

        // Act
        MvcResult result = mockMvc
            .perform(
                get("/courses/recommend")
                    .cookie(
                        new jakarta.servlet.http.Cookie(
                            "access_token",
                            invalidToken
                        )
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andReturn();

        // Assert
        ApiResponse<?> returnedResponse = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            ApiResponse.class
        );

        assertEquals(
            HttpStatus.BAD_REQUEST.value(),
            returnedResponse.getStatus()
        );
        assertEquals("Invalid access token", returnedResponse.getMessage());
    }
}
