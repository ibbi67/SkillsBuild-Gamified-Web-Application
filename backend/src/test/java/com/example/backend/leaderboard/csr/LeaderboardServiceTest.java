package com.example.backend.leaderboard.csr;

import com.example.backend.course.Course;
import com.example.backend.enrollment.Enrollment;
import com.example.backend.enrollment.csr.EnrollmentService;
import com.example.backend.leaderboard.LeaderboardDTO;
import com.example.backend.leaderboard.error.LeaderboardGetAllError;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class LeaderboardServiceTest {
    @Mock
    private PersonService personService;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAll() {
        when(personService.findAll()).thenReturn(new ArrayList<>());
        ServiceResult<List<LeaderboardDTO>, LeaderboardGetAllError> result = leaderboardService.getAll();
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(LeaderboardGetAllError.LEADERBOARD_NOT_FOUND, result.getError());
    }

    @Test
    public void testGetAllSuccess() {
        List<Person> persons = List.of(new Person("user1", "password", 5, LocalDate.now()));
        Course course = new Course();
        course.setDifficulty(3);
        List<Enrollment> enrollments = List.of(new Enrollment(course, persons.getFirst()));
        when(personService.findAll()).thenReturn(persons);
        when(enrollmentService.findByPersonId(1L)).thenReturn(enrollments);
        ServiceResult<List<LeaderboardDTO>, LeaderboardGetAllError> result = leaderboardService.getAll();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        assertEquals("user1", result.getData().getFirst().getUsername());
    }
}