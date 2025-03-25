package com.example.backend.goals;

import com.example.backend.goals.csr.GoalRepository;
import com.example.backend.goals.csr.GoalService;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GoalIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private GoalService goalService;

    private Person testPerson;
    private Goal testGoal;

    @BeforeEach
    void setUp() {
        // Create and save a test person
        testPerson = new Person("integrationuser", "password");
        testPerson = personRepository.save(testPerson);

        // Create and save a test goal
        testGoal = new Goal(
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                "Integration Test Goal",
                "Test reward",
                false
        );
        testGoal.setPerson(testPerson);

        // Add some courses
        Map<Integer, Boolean> courses = new HashMap<>();
        courses.put(1, true);
        courses.put(2, false);
        testGoal.setCourses(courses);

        testGoal = goalRepository.save(testGoal);
    }

    @AfterEach
    void tearDown() {
        goalRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    void createGoal_ShouldSaveAndReturnGoal() throws Exception {
        Goal newGoal = new Goal(
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                "New Integration Goal",
                "New reward",
                false
        );

        String response = mockMvc.perform(post("/api/goals?personId=" + testPerson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGoal)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("New Integration Goal")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Goal createdGoal = objectMapper.readValue(response, Goal.class);

        // Verify the goal was saved in the database
        assertTrue(goalRepository.findById(createdGoal.getId()).isPresent());
    }

    @Test
    void getGoalsByPerson_ShouldReturnGoalsList() throws Exception {
        mockMvc.perform(get("/api/goals/person/" + testPerson.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].description", is("Integration Test Goal")));
    }

    @Test
    void getGoalById_ShouldReturnGoal() throws Exception {
        mockMvc.perform(get("/api/goals/" + testGoal.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testGoal.getId().intValue())))
                .andExpect(jsonPath("$.description", is("Integration Test Goal")));
    }

    @Test
    void addCoursesToGoal_ShouldUpdateGoal() throws Exception {
        Map<Integer, Boolean> newCourses = new HashMap<>();
        newCourses.put(3, false);
        newCourses.put(4, true);

        mockMvc.perform(post("/api/goals/" + testGoal.getId() + "/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourses)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses", aMapWithSize(4)))
                .andExpect(jsonPath("$.courses.3", is(false)))
                .andExpect(jsonPath("$.courses.4", is(true)));

        // Verify the courses were added in the database
        Goal updatedGoal = goalRepository.findById(testGoal.getId()).orElseThrow();
        assertEquals(4, updatedGoal.getCourses().size());
        assertTrue(updatedGoal.getCourses().containsKey(3));
        assertTrue(updatedGoal.getCourses().containsKey(4));
    }

    @Test
    void deleteGoal_ShouldRemoveGoalFromDatabase() throws Exception {
        mockMvc.perform(delete("/api/goals/" + testGoal.getId()))
                .andExpect(status().isNoContent());

        // Verify the goal was deleted
        assertFalse(goalRepository.findById(testGoal.getId()).isPresent());
    }

    @Test
    void getGoalsForDashboard_ShouldReturnGoalsWithProgress() throws Exception {
        mockMvc.perform(get("/api/goals/dashboard/" + testPerson.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Goals retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data[0].id", is(testGoal.getId().intValue())))
                .andExpect(jsonPath("$.data[0].description", is("Integration Test Goal")))
                .andExpect(jsonPath("$.data[0].progress", closeTo(50.0, 0.1)))
                .andExpect(jsonPath("$.data[0].courses", aMapWithSize(2)));
    }

    @Test
    void completingAllCourses_ShouldDeleteGoal() throws Exception {
        // Mark course 2 as completed (course 1 is already completed)
        mockMvc.perform(put("/api/goals/" + testGoal.getId() + "/courses/2?completed=true"))
                .andExpect(status().isOk());

        // Now all courses are completed, so the goal should be deleted
        // Check that the goal no longer exists
        assertFalse(goalRepository.findById(testGoal.getId()).isPresent());
    }
}