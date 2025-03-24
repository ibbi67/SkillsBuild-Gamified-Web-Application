package com.example.backend.enrollment;

import com.example.backend.course.Course;
import com.example.backend.course.csr.CourseService;
import com.example.backend.enrollment.csr.EnrollmentRepository;
import com.example.backend.enrollment.csr.EnrollmentService;
import com.example.backend.enrollment.error.EnrollmentCreateError;
import com.example.backend.enrollment.error.EnrollmentGetAllError;
import com.example.backend.enrollment.error.EnrollmentGetByIdError;
import com.example.backend.person.Person;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class EnrollmentServiceTest {

    @Mock
    private JWT jwt;

    @Mock
    private CourseService courseService;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        String refreshToken = "validToken";
        Person person = new Person();
        person.setId(1L);
        Enrollment enrollment = new Enrollment();
        List<Enrollment> enrollments = List.of(enrollment);

        when(jwt.getPersonFromToken(refreshToken)).thenReturn(Optional.of(person));
        when(enrollmentRepository.findByPersonId(person.getId())).thenReturn(enrollments);

        ServiceResult<List<Enrollment>, EnrollmentGetAllError> result = enrollmentService.getAll(refreshToken);

        assertTrue(result.isSuccess());
        assertEquals(enrollments, result.getData());
    }

    @Test
    void testGetAll_InvalidRefreshToken() {
        String refreshToken = "invalidToken";

        when(jwt.getPersonFromToken(refreshToken)).thenReturn(Optional.empty());

        ServiceResult<List<Enrollment>, EnrollmentGetAllError> result = enrollmentService.getAll(refreshToken);

        assertFalse(result.isSuccess());
        assertEquals(EnrollmentGetAllError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    void testGetAll_NoEnrollmentsFound() {
        String refreshToken = "validToken";
        Person person = new Person();
        person.setId(1L);

        when(jwt.getPersonFromToken(refreshToken)).thenReturn(Optional.of(person));
        when(enrollmentRepository.findByPersonId(person.getId())).thenReturn(List.of());

        ServiceResult<List<Enrollment>, EnrollmentGetAllError> result = enrollmentService.getAll(refreshToken);

        assertFalse(result.isSuccess());
        assertEquals(EnrollmentGetAllError.ENROLLMENT_NOT_FOUND, result.getError());
    }

    @Test
    void testGetById() {
        Integer id = 1;
        Enrollment enrollment = new Enrollment();

        when(enrollmentRepository.findById(id)).thenReturn(Optional.of(enrollment));

        ServiceResult<Enrollment, EnrollmentGetByIdError> result = enrollmentService.getById(id);

        assertTrue(result.isSuccess());
        assertEquals(enrollment, result.getData());
    }

    @Test
    void testGetById_InvalidId() {
        Integer id = -1;

        ServiceResult<Enrollment, EnrollmentGetByIdError> result = enrollmentService.getById(id);

        assertFalse(result.isSuccess());
        assertEquals(EnrollmentGetByIdError.INVALID_ID, result.getError());
    }

    @Test
    void testGetById_EnrollmentNotFound() {
        Integer id = 1;

        when(enrollmentRepository.findById(id)).thenReturn(Optional.empty());

        ServiceResult<Enrollment, EnrollmentGetByIdError> result = enrollmentService.getById(id);

        assertFalse(result.isSuccess());
        assertEquals(EnrollmentGetByIdError.ENROLLMENT_NOT_FOUND, result.getError());
    }

    @Test
    void testCreate() {
        String refreshToken = "validToken";
        Integer courseId = 1;
        Person person = new Person();
        Course course = new Course();
        Enrollment enrollment = new Enrollment(course, person);

        when(jwt.getPersonFromToken(refreshToken)).thenReturn(Optional.of(person));
        when(courseService.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        ServiceResult<Enrollment, EnrollmentCreateError> result = enrollmentService.create(refreshToken, courseId);

        assertTrue(result.isSuccess());
        assertEquals(enrollment, result.getData());
    }

    @Test
    void testCreate_InvalidCourseId() {
        String refreshToken = "validToken";
        Integer invalidCourseId = null;

        ServiceResult<Enrollment, EnrollmentCreateError> result = enrollmentService.create(refreshToken, invalidCourseId);

        assertFalse(result.isSuccess());
        assertEquals(EnrollmentCreateError.INVALID_COURSE_ID, result.getError());
    }

    @Test
    void testCreate_InvalidRefreshToken() {
        String refreshToken = "invalidToken";
        Integer courseId = 1;

        when(jwt.getPersonFromToken(refreshToken)).thenReturn(Optional.empty());

        ServiceResult<Enrollment, EnrollmentCreateError> result = enrollmentService.create(refreshToken, courseId);

        assertFalse(result.isSuccess());
        assertEquals(EnrollmentCreateError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    void testCreate_CourseNotFound() {
        String refreshToken = "validToken";
        Integer courseId = 1;
        Person person = new Person();

        when(jwt.getPersonFromToken(refreshToken)).thenReturn(Optional.of(person));
        when(courseService.findById(courseId)).thenReturn(Optional.empty());

        ServiceResult<Enrollment, EnrollmentCreateError> result = enrollmentService.create(refreshToken, courseId);

        assertFalse(result.isSuccess());
        assertEquals(EnrollmentCreateError.COURSE_NOT_FOUND, result.getError());
    }
}
