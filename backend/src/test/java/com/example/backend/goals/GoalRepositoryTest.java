package com.example.backend.goals;

import com.example.backend.goals.Goal;
import com.example.backend.goals.csr.GoalRepository;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class GoalRepositoryTest {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private PersonRepository personRepository;

    private Person testPerson;
    private Goal activeGoal;
    private Goal expiredGoal;

    @BeforeEach
    void setUp() {
        // Create and save a test person
        testPerson = new Person("testuser", "password");
        testPerson = personRepository.save(testPerson);

        // Create an active goal
        activeGoal = new Goal(
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(2),
                "Active Goal",
                "Test reward",
                false
        );
        activeGoal.setPerson(testPerson);

        Map<Integer, Boolean> activeCourses = new HashMap<>();
        activeCourses.put(1, true);
        activeCourses.put(2, false);
        activeGoal.setCourses(activeCourses);

        // Create an expired goal
        expiredGoal = new Goal(
                LocalDate.now().minusMonths(6),
                LocalDate.now().minusMonths(1),
                "Expired Goal",
                "Test reward expired",
                false
        );
        expiredGoal.setPerson(testPerson);

        Map<Integer, Boolean> expiredCourses = new HashMap<>();
        expiredCourses.put(3, true);
        expiredCourses.put(4, true);
        expiredGoal.setCourses(expiredCourses);

        // Save the goals
        goalRepository.save(activeGoal);
        goalRepository.save(expiredGoal);
    }

    @AfterEach
    void tearDown() {
        goalRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    void findByPerson_ShouldReturnAllGoalsForPerson() {
        // When
        List<Goal> goals = goalRepository.findByPerson(testPerson);

        // Then
        assertEquals(2, goals.size());
        assertTrue(goals.stream().anyMatch(g -> g.getDescription().equals("Active Goal")));
        assertTrue(goals.stream().anyMatch(g -> g.getDescription().equals("Expired Goal")));
    }

    @Test
    void findByEndDateBefore_ShouldReturnExpiredGoals() {
        // When
        List<Goal> expiredGoals = goalRepository.findByEndDateBefore(LocalDate.now());

        // Then
        assertEquals(1, expiredGoals.size());
        assertEquals("Expired Goal", expiredGoals.get(0).getDescription());
    }

    @Test
    void save_ShouldPersistGoalWithCourses() {
        // Given
        Goal newGoal = new Goal(
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                "New Test Goal",
                "New reward",
                false
        );
        newGoal.setPerson(testPerson);

        Map<Integer, Boolean> courses = new HashMap<>();
        courses.put(5, false);
        courses.put(6, true);
        newGoal.setCourses(courses);

        // When
        Goal savedGoal = goalRepository.save(newGoal);

        // Then
        assertNotNull(savedGoal.getId());

        // Fetch from DB to verify
        Goal fetchedGoal = goalRepository.findById(savedGoal.getId()).orElseThrow();
        assertEquals("New Test Goal", fetchedGoal.getDescription());
        assertEquals(2, fetchedGoal.getCourses().size());
        assertTrue(fetchedGoal.getCourses().containsKey(5));
        assertTrue(fetchedGoal.getCourses().containsKey(6));
        assertFalse(fetchedGoal.isCourseCompleted(5));
        assertTrue(fetchedGoal.isCourseCompleted(6));
    }

    @Test
    void delete_ShouldRemoveGoal() {
        // Given
        Long goalId = activeGoal.getId();

        // When
        goalRepository.deleteById(goalId);

        // Then
        assertFalse(goalRepository.findById(goalId).isPresent());
        // Make sure we still have the other goal
        assertEquals(1, goalRepository.findAll().size());
    }
}