package com.example.backend.goals.csr;

import com.example.backend.enrollment.Enrollment;
import com.example.backend.enrollment.csr.EnrollmentService;
import com.example.backend.goals.AddEnrollmentDTO;
import com.example.backend.goals.Goal;
import com.example.backend.goals.GoalDTO;
import com.example.backend.goals.error.*;
import com.example.backend.person.Person;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GoalServiceTest {

    @Mock
    private JWT jwt;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private GoalService goalService;

    private Person testPerson;
    private Goal testGoal;
    private GoalDTO testGoalDTO;
    private final String validToken = "validToken";
    private final String invalidToken = "invalidToken";

    @BeforeEach
    void setUp() {
        testPerson = new Person("testUser", "password");
        testPerson.setId(1L);

        testGoal = new Goal(
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                "Test Goal",
                "Test Reward",
                testPerson
        );
        testGoal.setId(1L);

        testGoalDTO = new GoalDTO(LocalDate.now(), LocalDate.now().plusDays(30), "Test Goal", "Test Reward");

        // Setup JWT mock
        when(jwt.getPersonFromToken(validToken)).thenReturn(Optional.of(testPerson));
        when(jwt.getPersonFromToken(invalidToken)).thenReturn(Optional.empty());
    }

    // CREATE GOAL TESTS

    @Test
    void testCreateGoal_Success() {
        // Setup mocks
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // Execute
        ServiceResult<Void, GoalCreateError> result = goalService.createGoal(validToken, testGoalDTO);

        // Verify
        assertTrue(result.isSuccess());
        assertNull(result.getError());
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void testCreateGoal_InvalidAccessToken() {
        // Execute
        ServiceResult<Void, GoalCreateError> result = goalService.createGoal(invalidToken, testGoalDTO);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalCreateError.INVALID_ACCESS_TOKEN, result.getError());
        verify(jwt).getPersonFromToken(invalidToken);
        verify(goalRepository, never()).save(any(Goal.class));
    }

    // DELETE GOAL TESTS

    @Test
    void testDeleteGoal_Success() {
        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(testGoal));
        doNothing().when(goalRepository).deleteById(anyLong());

        // Execute
        ServiceResult<Void, GoalDeleteError> result = goalService.deleteGoal(validToken, 1L);

        // Verify
        assertTrue(result.isSuccess());
        assertNull(result.getError());
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).findById(1L);
        verify(goalRepository).deleteById(1L);
    }

    @Test
    void testDeleteGoal_InvalidAccessToken() {
        // Execute
        ServiceResult<Void, GoalDeleteError> result = goalService.deleteGoal(invalidToken, 1L);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalDeleteError.INVALID_ACCESS_TOKEN, result.getError());
        verify(jwt).getPersonFromToken(invalidToken);
        verify(goalRepository, never()).findById(anyLong());
        verify(goalRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteGoal_GoalNotFound() {
        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execute
        ServiceResult<Void, GoalDeleteError> result = goalService.deleteGoal(validToken, 1L);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalDeleteError.GOAL_NOT_FOUND, result.getError());
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).findById(1L);
        verify(goalRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteGoal_PermissionDenied() {
        // Create a different person who doesn't own the goal
        Person differentPerson = new Person("otherUser", "password");
        differentPerson.setId(2L);

        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(testGoal));
        when(jwt.getPersonFromToken(validToken)).thenReturn(Optional.of(differentPerson));

        // Execute
        ServiceResult<Void, GoalDeleteError> result = goalService.deleteGoal(validToken, 1L);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalDeleteError.PERMISSION_DENIED, result.getError());
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).findById(1L);
        verify(goalRepository, never()).deleteById(anyLong());
    }

    // ADD ENROLLMENT TO GOAL TESTS

    @Test
    void testAddEnrollmentToGoal_Success() {
        // Setup enrollments
        Enrollment enrollment1 = new Enrollment();
        enrollment1.setId(1);
        Enrollment enrollment2 = new Enrollment();
        enrollment2.setId(2);

        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(testGoal));
        when(enrollmentService.findById(1)).thenReturn(Optional.of(enrollment1));
        when(enrollmentService.findById(2)).thenReturn(Optional.of(enrollment2));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // Create AddEnrollmentDTO
        AddEnrollmentDTO addEnrollmentDTO = new AddEnrollmentDTO(Arrays.asList(1, 2));

        // Execute
        ServiceResult<Goal, GoalAddEnrollmentError> result = goalService.addEnrollmentToGoal(1L, addEnrollmentDTO);

        // Verify
        assertTrue(result.isSuccess());
        assertNull(result.getError());
        assertEquals(testGoal, result.getData());
        verify(goalRepository).findById(1L);
        verify(enrollmentService).findById(1);
        verify(enrollmentService).findById(2);
        verify(goalRepository).save(testGoal);
    }

    @Test
    void testAddEnrollmentToGoal_GoalNotFound() {
        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Create AddEnrollmentDTO
        AddEnrollmentDTO addEnrollmentDTO = new AddEnrollmentDTO(Arrays.asList(1, 2));

        // Execute
        ServiceResult<Goal, GoalAddEnrollmentError> result = goalService.addEnrollmentToGoal(1L, addEnrollmentDTO);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalAddEnrollmentError.GOAL_NOT_FOUND, result.getError());
        verify(goalRepository).findById(1L);
        verify(enrollmentService, never()).findById(anyInt());
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void testAddEnrollmentToGoal_EnrollmentsNotFound() {
        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(testGoal));
        when(enrollmentService.findById(anyInt())).thenReturn(Optional.empty());

        // Create AddEnrollmentDTO
        AddEnrollmentDTO addEnrollmentDTO = new AddEnrollmentDTO(Arrays.asList(1, 2));

        // Execute
        ServiceResult<Goal, GoalAddEnrollmentError> result = goalService.addEnrollmentToGoal(1L, addEnrollmentDTO);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalAddEnrollmentError.ENROLLMENTS_NOT_FOUND, result.getError());
        verify(goalRepository).findById(1L);
        verify(enrollmentService, atLeastOnce()).findById(anyInt());
        verify(goalRepository, never()).save(any(Goal.class));
    }

    // GET GOALS TESTS

    @Test
    void testGetGoals_Success() {
        // Setup goals list
        List<Goal> goals = Collections.singletonList(testGoal);

        // Setup mocks
        when(goalRepository.findByPersonId(anyLong())).thenReturn(goals);

        // Execute
        ServiceResult<List<Goal>, GoalGetError> result = goalService.getGoals(validToken);

        // Verify
        assertTrue(result.isSuccess());
        assertNull(result.getError());
        assertEquals(goals, result.getData());
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).findByPersonId(1L);
    }

    @Test
    void testGetGoals_InvalidAccessToken() {
        // Execute
        ServiceResult<List<Goal>, GoalGetError> result = goalService.getGoals(invalidToken);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalGetError.INVALID_ACCESS_TOKEN, result.getError());
        verify(jwt).getPersonFromToken(invalidToken);
        verify(goalRepository, never()).findByPersonId(anyLong());
    }
    
    @Test
    void testGetGoals_EmptyList() {
        // Setup empty goals list
        List<Goal> emptyGoals = new ArrayList<>();

        // Setup mocks
        when(goalRepository.findByPersonId(anyLong())).thenReturn(emptyGoals);

        // Execute
        ServiceResult<List<Goal>, GoalGetError> result = goalService.getGoals(validToken);

        // Verify
        assertTrue(result.isSuccess());
        assertEquals(emptyGoals, result.getData());
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).findByPersonId(1L);
    }

    // UPDATE ENROLLMENT COMPLETION STATUS TESTS

    @Test
    void testUpdateEnrollmentCompletionStatus_Success() {
        // Setup enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1);
        enrollment.setCompleted(false);
        
        // Add enrollment to goal
        testGoal.addEnrollment(enrollment);

        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(testGoal));
        when(enrollmentService.findById(anyInt())).thenReturn(Optional.of(enrollment));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // Execute
        ServiceResult<Goal, GoalUpdateEnrollmentCompletionStatusError> result = 
            goalService.updateEnrollmentCompletionStatus(validToken, 1L, 1);

        // Verify
        assertTrue(result.isSuccess());
        assertNull(result.getError());
        assertEquals(testGoal, result.getData());
        assertTrue(enrollment.getCompleted()); // Status should be toggled
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).findById(1L);
        verify(enrollmentService).findById(1);
        verify(goalRepository).save(testGoal);
    }

    @Test
    void testUpdateEnrollmentCompletionStatus_InvalidAccessToken() {
        // Execute
        ServiceResult<Goal, GoalUpdateEnrollmentCompletionStatusError> result = 
            goalService.updateEnrollmentCompletionStatus(invalidToken, 1L, 1);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalUpdateEnrollmentCompletionStatusError.INVALID_ACCESS_TOKEN, result.getError());
        verify(jwt).getPersonFromToken(invalidToken);
        verify(goalRepository, never()).findById(anyLong());
        verify(enrollmentService, never()).findById(anyInt());
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void testUpdateEnrollmentCompletionStatus_GoalNotFound() {
        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Execute
        ServiceResult<Goal, GoalUpdateEnrollmentCompletionStatusError> result = 
            goalService.updateEnrollmentCompletionStatus(validToken, 1L, 1);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalUpdateEnrollmentCompletionStatusError.GOAL_NOT_FOUND, result.getError());
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).findById(1L);
        verify(enrollmentService, never()).findById(anyInt());
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void testUpdateEnrollmentCompletionStatus_PermissionDenied() {
        // Create a different person who doesn't own the goal
        Person differentPerson = new Person("otherUser", "password");
        differentPerson.setId(2L);

        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(testGoal));
        when(jwt.getPersonFromToken(validToken)).thenReturn(Optional.of(differentPerson));

        // Execute
        ServiceResult<Goal, GoalUpdateEnrollmentCompletionStatusError> result = 
            goalService.updateEnrollmentCompletionStatus(validToken, 1L, 1);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalUpdateEnrollmentCompletionStatusError.PERMISSION_DENIED, result.getError());
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).findById(1L);
        verify(enrollmentService, never()).findById(anyInt());
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void testUpdateEnrollmentCompletionStatus_EnrollmentNotFound() {
        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(testGoal));
        when(enrollmentService.findById(anyInt())).thenReturn(Optional.empty());

        // Execute
        ServiceResult<Goal, GoalUpdateEnrollmentCompletionStatusError> result = 
            goalService.updateEnrollmentCompletionStatus(validToken, 1L, 1);

        // Verify
        assertFalse(result.isSuccess());
        assertEquals(GoalUpdateEnrollmentCompletionStatusError.ENROLLMENTS_NOT_FOUND, result.getError());
        verify(jwt).getPersonFromToken(validToken);
        verify(goalRepository).findById(1L);
        verify(enrollmentService).findById(1);
        verify(goalRepository, never()).save(any(Goal.class));
    }

    // HELPER METHOD TESTS

    @Test
    void testSaveGoal() {
        // Setup mocks
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // Execute
        Optional<Goal> result = goalService.save(testGoal);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(testGoal, result.get());
        verify(goalRepository).save(testGoal);
    }

    @Test
    void testSaveGoalDTO() {
        // Setup mocks
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        // Execute
        Optional<Goal> result = goalService.save(testGoalDTO, testPerson);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(testGoal.getDescription(), result.get().getDescription());
        assertEquals(testGoal.getStartDate(), result.get().getStartDate());
        assertEquals(testGoal.getEndDate(), result.get().getEndDate());
        assertEquals(testGoal.getReward(), result.get().getReward());
        assertEquals(testGoal.getPerson(), result.get().getPerson());
        verify(goalRepository).save(any(Goal.class));
    }

    @Test
    void testDeleteById() {
        // Setup mocks
        doNothing().when(goalRepository).deleteById(anyLong());

        // Execute
        goalService.deleteById(1L);

        // Verify
        verify(goalRepository).deleteById(1L);
    }

    @Test
    void testFindById() {
        // Setup mocks
        when(goalRepository.findById(anyLong())).thenReturn(Optional.of(testGoal));

        // Execute
        Optional<Goal> result = goalService.findById(1L);

        // Verify
        assertTrue(result.isPresent());
        assertEquals(testGoal, result.get());
        verify(goalRepository).findById(1L);
    }

    @Test
    void testFindByPersonId() {
        // Setup goals list
        List<Goal> goals = Collections.singletonList(testGoal);

        // Setup mocks
        when(goalRepository.findByPersonId(anyLong())).thenReturn(goals);

        // Execute
        List<Goal> result = goalService.findByPersonId(1L);

        // Verify
        assertEquals(goals, result);
        verify(goalRepository).findByPersonId(1L);
    }
}