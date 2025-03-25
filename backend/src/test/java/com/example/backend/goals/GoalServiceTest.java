package com.example.backend.goals;

import com.example.backend.goals.Goal;
import com.example.backend.goals.GoalProgressDTO;
import com.example.backend.goals.csr.GoalRepository;
import com.example.backend.goals.csr.GoalService;
import com.example.backend.person.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private Person testPerson;
    private Goal testGoal;
    private Map<Integer, Boolean> testCourses;

    @BeforeEach
    void setUp() {
        testPerson = new Person("testuser", "password");
        testPerson.setId(1L);

        testCourses = new HashMap<>();
        testCourses.put(1, true);
        testCourses.put(2, false);
        testCourses.put(3, false);

        testGoal = new Goal(
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                "Learn Spring Boot",
                "Career advancement",
                false
        );
        testGoal.setId(1L);
        testGoal.setPerson(testPerson);
        testGoal.setCourses(testCourses);
    }

    @Test
    void createGoal_ShouldSaveAndReturnGoal() {
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        Goal result = goalService.createGoal(testGoal);

        assertNotNull(result);
        assertEquals(testGoal.getId(), result.getId());
        assertEquals(testGoal.getDescription(), result.getDescription());
        verify(goalRepository, times(1)).save(testGoal);
    }

    @Test
    void getGoalsByPerson_ShouldReturnListOfGoals() {
        when(goalRepository.findByPerson(testPerson)).thenReturn(Collections.singletonList(testGoal));

        List<Goal> result = goalService.getGoalsByPerson(testPerson);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testGoal.getId(), result.get(0).getId());
        verify(goalRepository, times(1)).findByPerson(testPerson);
    }

    @Test
    void getGoalById_WhenGoalExists_ShouldReturnGoal() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        Optional<Goal> result = goalService.getGoalById(1L);

        assertTrue(result.isPresent());
        assertEquals(testGoal.getId(), result.get().getId());
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    void getGoalById_WhenGoalDoesNotExist_ShouldReturnEmptyOptional() {
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Goal> result = goalService.getGoalById(999L);

        assertFalse(result.isPresent());
        verify(goalRepository, times(1)).findById(999L);
    }

    @Test
    void deleteGoal_ShouldCallRepositoryDeleteById() {
        doNothing().when(goalRepository).deleteById(1L);

        goalService.deleteGoal(1L);

        verify(goalRepository, times(1)).deleteById(1L);
    }

    @Test
    void addCoursesToGoal_WhenGoalExists_ShouldAddCoursesAndSave() {
        Map<Integer, Boolean> newCourses = new HashMap<>();
        newCourses.put(4, false);
        newCourses.put(5, true);

        Goal updatedGoal = new Goal();
        updatedGoal.setId(testGoal.getId());
        updatedGoal.setCourses(new HashMap<>(testGoal.getCourses()));
        updatedGoal.getCourses().putAll(newCourses);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(updatedGoal);

        Goal result = goalService.addCoursesToGoal(1L, newCourses);

        assertNotNull(result);
        assertEquals(5, result.getCourses().size());
        assertTrue(result.getCourses().containsKey(4));
        assertTrue(result.getCourses().containsKey(5));
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(any(Goal.class));
    }

    @Test
    void addCoursesToGoal_WhenGoalDoesNotExist_ShouldReturnNull() {
        Map<Integer, Boolean> newCourses = new HashMap<>();
        newCourses.put(4, false);

        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        Goal result = goalService.addCoursesToGoal(999L, newCourses);

        assertNull(result);
        verify(goalRepository, times(1)).findById(999L);
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void updateCourseStatus_WhenGoalExistsAndNotAllCoursesCompleted_ShouldUpdateAndSave() {
        Goal updatedGoal = new Goal();
        updatedGoal.setId(testGoal.getId());

        Map<Integer, Boolean> updatedCourses = new HashMap<>(testGoal.getCourses());
        updatedCourses.put(2, true);  // Change course 2 to completed
        updatedGoal.setCourses(updatedCourses);

        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        when(goalRepository.save(any(Goal.class))).thenReturn(updatedGoal);

        Goal result = goalService.updateCourseStatus(1L, 2, true);

        assertNotNull(result);
        assertTrue(result.getCourses().get(2));
        verify(goalRepository, times(1)).findById(1L);
        verify(goalRepository, times(1)).save(any(Goal.class));
        verify(goalRepository, never()).deleteById(any());
    }

    @Test
    void updateCourseStatus_WhenAllCoursesCompleted_ShouldDeleteGoalAndReturnNull() {
        // Create a goal where this update will complete all courses
        Goal goalWithLastCourse = new Goal();
        goalWithLastCourse.setId(2L);

        Map<Integer, Boolean> almostCompletedCourses = new HashMap<>();
        almostCompletedCourses.put(1, true);
        almostCompletedCourses.put(2, true);
        almostCompletedCourses.put(3, false);  // This is the only incomplete course
        goalWithLastCourse.setCourses(almostCompletedCourses);

        when(goalRepository.findById(2L)).thenReturn(Optional.of(goalWithLastCourse));
        doNothing().when(goalRepository).deleteById(2L);

        Goal result = goalService.updateCourseStatus(2L, 3, true);

        assertNull(result);
        verify(goalRepository, times(1)).findById(2L);
        verify(goalRepository, never()).save(any(Goal.class));
        verify(goalRepository, times(1)).deleteById(2L);
    }

    @Test
    void getGoalProgress_ShouldCalculateCorrectPercentage() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));

        double result = goalService.getGoalProgress(1L);

        // 1 out of 3 courses is completed (33.33%)
        assertEquals(33.33, result, 0.01);
        verify(goalRepository, times(1)).findById(1L);
    }

    @Test
    void getGoalProgress_WhenNoCourses_ShouldReturnZero() {
        Goal goalWithNoCourses = new Goal();
        goalWithNoCourses.setId(3L);
        goalWithNoCourses.setCourses(new HashMap<>());

        when(goalRepository.findById(3L)).thenReturn(Optional.of(goalWithNoCourses));

        double result = goalService.getGoalProgress(3L);

        assertEquals(0.0, result);
        verify(goalRepository, times(1)).findById(3L);
    }

    @Test
    void getGoalProgress_WhenGoalDoesNotExist_ShouldReturnZero() {
        when(goalRepository.findById(999L)).thenReturn(Optional.empty());

        double result = goalService.getGoalProgress(999L);

        assertEquals(0.0, result);
        verify(goalRepository, times(1)).findById(999L);
    }

    @Test
    void getAllGoalsWithProgress_ShouldReturnCorrectDTOs() {
        when(goalRepository.findByPerson(testPerson)).thenReturn(Collections.singletonList(testGoal));

        List<GoalProgressDTO> result = goalService.getAllGoalsWithProgress(testPerson);

        assertNotNull(result);
        assertEquals(1, result.size());

        GoalProgressDTO dto = result.get(0);
        assertEquals(testGoal.getId(), dto.getId());
        assertEquals(testGoal.getDescription(), dto.getDescription());
        assertEquals(testGoal.getCourses(), dto.getCourses());
        assertEquals(33.33, dto.getProgress(), 0.01); // 1 out of 3 courses completed

        verify(goalRepository, times(1)).findByPerson(testPerson);
    }

    @Test
    void getAllGoalsWithProgress_WhenNoGoals_ShouldReturnEmptyList() {
        when(goalRepository.findByPerson(testPerson)).thenReturn(Collections.emptyList());

        List<GoalProgressDTO> result = goalService.getAllGoalsWithProgress(testPerson);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(goalRepository, times(1)).findByPerson(testPerson);
    }

    @Test
    void deleteExpiredGoals_ShouldDeleteGoalsPastEndDate() {
        LocalDate today = LocalDate.now();
        List<Goal> expiredGoals = Arrays.asList(
                new Goal(today.minusMonths(6), today.minusMonths(3), "Old Goal 1", "Reward", false),
                new Goal(today.minusMonths(4), today.minusMonths(1), "Old Goal 2", "Reward", false)
        );

        when(goalRepository.findByEndDateBefore(any(LocalDate.class))).thenReturn(expiredGoals);
        doNothing().when(goalRepository).deleteAll(expiredGoals);

        goalService.deleteExpiredGoals();

        verify(goalRepository, times(1)).findByEndDateBefore(any(LocalDate.class));
        verify(goalRepository, times(1)).deleteAll(expiredGoals);
    }
}
