package com.example.backend.course.csr;

import com.example.backend.course.Course;
import com.example.backend.course.error.CourseGetAllError;
import com.example.backend.course.error.CourseGetByIdError;
import com.example.backend.course.error.CourseGetRecommendError;
import com.example.backend.person.Person;
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
import static org.mockito.Mockito.*;

public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private JWT jwt;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCourses() {
        Course course1 = new Course();
        Course course2 = new Course();
        List<Course> courses = Arrays.asList(course1, course2);
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.findAll();
        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    public void testGetCourseById() {
        Course course = new Course();
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        Optional<Course> result = courseService.findById(1);
        assertTrue(result.isPresent());
        verify(courseRepository, times(1)).findById(1);
    }

    @Test
    public void testGetAllSuccess() {
        Course course1 = new Course();
        Course course2 = new Course();
        List<Course> courses = Arrays.asList(course1, course2);
        when(courseRepository.findAll()).thenReturn(courses);

        ServiceResult<List<Course>, CourseGetAllError> result = courseService.getAll();
        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    public void testGetAllFailure() {
        when(courseRepository.findAll()).thenReturn(List.of());

        ServiceResult<List<Course>, CourseGetAllError> result = courseService.getAll();
        assertFalse(result.isSuccess());
        assertEquals(CourseGetAllError.GET_ALL_COURSES_FAILED, result.getError());
    }

    @Test
    public void testGetByIdSuccess() {
        Course course = new Course();
        when(courseRepository.findById(1)).thenReturn(Optional.of(course));

        ServiceResult<Course, CourseGetByIdError> result = courseService.getById(1);
        assertTrue(result.isSuccess());
        assertEquals(course, result.getData());
    }

    @Test
    public void testGetByIdFailure() {
        when(courseRepository.findById(1)).thenReturn(Optional.empty());

        ServiceResult<Course, CourseGetByIdError> result = courseService.getById(1);
        assertFalse(result.isSuccess());
        assertEquals(CourseGetByIdError.COURSE_NOT_FOUND, result.getError());
    }

    @Test
    public void testGetByIdNullId() {
        ServiceResult<Course, CourseGetByIdError> result = courseService.getById(null);
        assertFalse(result.isSuccess());
        assertEquals(CourseGetByIdError.GET_COURSE_BY_ID_FAILED, result.getError());
    }

    @Test
    public void testGetRecommendedCourses_invalidAccessToken() {
        String accessToken = "invalidToken";
        when(jwt.getPersonFromToken(accessToken)).thenReturn(Optional.empty());

        ServiceResult<List<Course>, CourseGetRecommendError> result = courseService.getRecommendedCourses(accessToken);

        assertFalse(result.isSuccess());
        assertEquals(CourseGetRecommendError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    public void testGetRecommendedCourses_noFavoriteCourses() {
        String accessToken = "validToken";
        Person person = new Person();
        person.setFavoriteCourses(List.of());
        when(jwt.getPersonFromToken(accessToken)).thenReturn(Optional.of(person));

        ServiceResult<List<Course>, CourseGetRecommendError> result = courseService.getRecommendedCourses(accessToken);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testGetRecommendedCourses_noRecommendedCourses() {
        String accessToken = "validToken";
        Person person = new Person();
        Course favoriteCourse = new Course();
        favoriteCourse.setDifficulty(3);
        person.setFavoriteCourses(List.of(favoriteCourse));
        when(jwt.getPersonFromToken(accessToken)).thenReturn(Optional.of(person));
        when(courseRepository.findAll()).thenReturn(List.of(favoriteCourse));

        ServiceResult<List<Course>, CourseGetRecommendError> result = courseService.getRecommendedCourses(accessToken);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testGetRecommendedCourses_success() {
        String accessToken = "validToken";
        Person person = new Person();
        Course favoriteCourse = new Course();
        favoriteCourse.setDifficulty(3);
        person.setFavoriteCourses(List.of(favoriteCourse));
        Course recommendedCourse = new Course();
        recommendedCourse.setDifficulty(3);
        recommendedCourse.setTitle("Recommended Course");
        when(jwt.getPersonFromToken(accessToken)).thenReturn(Optional.of(person));
        when(courseRepository.findAll()).thenReturn(List.of(favoriteCourse, recommendedCourse));

        ServiceResult<List<Course>, CourseGetRecommendError> result = courseService.getRecommendedCourses(accessToken);

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
        assertEquals(1, result.getData().size());
        assertEquals(recommendedCourse, result.getData().getFirst());
    }
}
