package com.example.backend.service;

import com.example.backend.course.Course;
import com.example.backend.course.csr.CourseRepository;
import com.example.backend.course.csr.CourseService;
import com.example.backend.course.error.CourseGetTrendingError;
import com.example.backend.course.error.CourseViewError;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TrendingServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private JWT jwt;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTrendingCourses() {
        Course course1 = new Course("Course 1", "Description 1", "link1", 30, 1);
        Course course2 = new Course("Course 2", "Description 2", "link2", 60, 2);
        List<Course> expectedCourses = Arrays.asList(course1, course2);

        when(courseRepository.findTop10ByOrderByViewsDesc()).thenReturn(expectedCourses);


        ServiceResult<List<Course>, CourseGetTrendingError> result = courseService.getTrendingCourses();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(expectedCourses, result.getData());
    }

    @Test
    public void testGetTrendingCoursesFailed() {

        when(courseRepository.findTop10ByOrderByViewsDesc()).thenThrow(new RuntimeException("Database error"));


        ServiceResult<List<Course>, CourseGetTrendingError> result = courseService.getTrendingCourses();
        assertFalse(result.isSuccess());
        assertNull(result.getData());
        assertEquals(CourseGetTrendingError.GET_TRENDING_COURSES_FAILED, result.getError());
    }

    @Test
    public void testIncrementCourseViews() {

        Course course = new Course("Course 1", "Description 1", "link1", 30, 1);
        course.setId(1);
        course.setViews(5);

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));


        ServiceResult<Void, CourseViewError> result = courseService.incrementCourseViews(1);
        assertTrue(result.isSuccess());
        assertEquals(6, course.getViews());
        verify(courseRepository).save(course);
    }

    @Test
    public void testIncrementCourseViewsNotFound() {

        when(courseRepository.findById(999)).thenReturn(Optional.empty());


        ServiceResult<Void, CourseViewError> result = courseService.incrementCourseViews(999);
        assertFalse(result.isSuccess());
        assertEquals(CourseViewError.COURSE_NOT_FOUND, result.getError());
        verify(courseRepository, never()).save(any());
    }

    @Test
    public void testIncrementCourseViewsInvalidId() {
        ServiceResult<Void, CourseViewError> result = courseService.incrementCourseViews(null);
        assertFalse(result.isSuccess());
        assertEquals(CourseViewError.COURSE_NOT_FOUND, result.getError());

        result = courseService.incrementCourseViews(-1);
        assertFalse(result.isSuccess());
        assertEquals(CourseViewError.COURSE_NOT_FOUND, result.getError());
    }
}