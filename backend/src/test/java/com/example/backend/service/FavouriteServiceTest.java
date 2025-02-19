package com.example.backend.service;

import com.example.backend.dao.FavouriteCourseDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private FavouriteService favouriteService;

    private User user;
    private Course course;
    private FavouriteCourseDao favouriteCourseDao;
    private final String validToken = "valid_token";
    private final String invalidToken = "invalid_token";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setFavouriteCourses(new ArrayList<>());

        course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");

        favouriteCourseDao = new FavouriteCourseDao(course.getId());

        given(jwtService.verifyToken(validToken)).willReturn(true);
        given(jwtService.verifyToken(invalidToken)).willReturn(false);
        given(jwtService.getUserDetails(validToken)).willReturn(user);
        given(userService.findByUsername("testuser")).willReturn(user);
        given(courseService.getCourseById(1L)).willReturn(course);
    }

    @Test
    void getFavourite_WithValidToken_ShouldReturnCourses() {
        // Arrange
        user.getFavouriteCourses().add(course);

        // Act
        ApiResponse<List<Course>> response = favouriteService.getFavourite(validToken);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Favourite courses found", response.getMessage());
        assertEquals(1, response.getData().size());
    }

    @Test
    void getFavourite_WithInvalidToken_ShouldReturnError() {
        // Act
        ApiResponse<List<Course>> response = favouriteService.getFavourite(invalidToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid access token", response.getMessage());
    }

    @Test
    void addFavourite_WithValidToken_ShouldAddCourse() {
        // Act
        ApiResponse<Void> response = favouriteService.addFavourite(validToken, favouriteCourseDao);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Favourite course added successfully", response.getMessage());
        assertTrue(user.getFavouriteCourses().contains(course));
        verify(userService).save(user);
    }

    @Test
    void addFavourite_WithDuplicateCourse_ShouldReturnError() {
        // Arrange
        user.getFavouriteCourses().add(course);

        // Act
        ApiResponse<Void> response = favouriteService.addFavourite(validToken, favouriteCourseDao);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Course already added to favourite", response.getMessage());
    }

    @Test
    void removeFavourite_WithValidToken_ShouldRemoveCourse() {
        // Arrange
        user.getFavouriteCourses().add(course);

        // Act
        ApiResponse<Void> response = favouriteService.removeFavourite(validToken, favouriteCourseDao);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Favourite course removed successfully", response.getMessage());
        assertFalse(user.getFavouriteCourses().contains(course));
        verify(userService).save(user);
    }

    @Test
    void removeFavourite_WithInvalidToken_ShouldReturnError() {
        // Act
        ApiResponse<Void> response = favouriteService.removeFavourite(invalidToken, favouriteCourseDao);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid access token", response.getMessage());
    }
}
