package com.example.backend.goals;


import com.example.backend.goals.Goal;
import com.example.backend.goals.GoalProgressDTO;
import com.example.backend.goals.csr.GoalController;
import com.example.backend.goals.csr.GoalService;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalController.class)
public class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GoalService goalService;

    @MockBean
    private PersonService personService;

    private Person testPerson;
    private Goal testGoal;
    private Map<Integer, Boolean> testCourses;
    private List<GoalProgressDTO> testGoalProgressList;

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

        // Create test goal progress DTOs
        GoalProgressDTO progressDTO = new GoalProgressDTO(
                testGoal.getId(),
                testGoal.getDescription(),
                testGoal.getStartDate(),
                testGoal.getEndDate(),
                testGoal.getReward(),
                33.33, // 1 out of 3 courses completed
                testGoal.getCourses()
        );
        testGoalProgressList = Collections.singletonList(progressDTO);
    }

    @Test
    void createGoal_ShouldReturnCreatedGoal() throws Exception {
        when(personService.getPersonById(1L)).thenReturn(Optional.of(testPerson));
        when(goalService.createGoal(any(Goal.class))).thenReturn(testGoal);

        mockMvc.perform(post("/api/goals?personId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testGoal)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Learn Spring Boot")))
                .andExpect(jsonPath("$.reward", is("Career advancement")));

        verify(personService, times(1)).getPersonById(1L);
        verify(goalService, times(1)).createGoal(any(Goal.class));
    }

    @Test
    void createGoal_WhenPersonNotFound_ShouldReturnNotFound() throws Exception {
        when(personService.getPersonById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/goals?personId=999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testGoal)))
                .andExpect(status().isNotFound());

        verify(personService, times(1)).getPersonById(999L);
        verify(goalService, never()).createGoal(any(Goal.class));
    }

    @Test
    void getGoalsByPerson_ShouldReturnGoalsList() throws Exception {
        when(personService.getPersonById(1L)).thenReturn(Optional.of(testPerson));
        when(goalService.getGoalsByPerson(testPerson)).thenReturn(Collections.singletonList(testGoal));

        mockMvc.perform(get("/api/goals/person/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Learn Spring Boot")));

        verify(personService, times(1)).getPersonById(1L);
        verify(goalService, times(1)).getGoalsByPerson(testPerson);
    }

    @Test
    void getGoalsByPerson_WhenPersonNotFound_ShouldReturnNotFound() throws Exception {
        when(personService.getPersonById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/goals/person/999"))
                .andExpect(status().isNotFound());

        verify(personService, times(1)).getPersonById(999L);
        verify(goalService, never()).getGoalsByPerson(any(Person.class));
    }

    @Test
    void getGoalById_WhenGoalExists_ShouldReturnGoal() throws Exception {
        when(goalService.getGoalById(1L)).thenReturn(Optional.of(testGoal));

        mockMvc.perform(get("/api/goals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Learn Spring Boot")));

        verify(goalService, times(1)).getGoalById(1L);
    }

    @Test
    void getGoalById_WhenGoalDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(goalService.getGoalById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/goals/999"))
                .andExpect(status().isNotFound());

        verify(goalService, times(1)).getGoalById(999L);
    }

    @Test
    void deleteGoal_ShouldReturnNoContent() throws Exception {
        doNothing().when(goalService).deleteGoal(1L);

        mockMvc.perform(delete("/api/goals/1"))
                .andExpect(status().isNoContent());

        verify(goalService, times(1)).deleteGoal(1L);
    }

    @Test
    void addCoursesToGoal_WhenGoalExists_ShouldReturnUpdatedGoal() throws Exception {
        Map<Integer, Boolean> newCourses = new HashMap<>();
        newCourses.put(4, false);
        newCourses.put(5, true);

        Goal updatedGoal = new Goal();
        updatedGoal.setId(testGoal.getId());
        updatedGoal.setCourses(new HashMap<>(testGoal.getCourses()));
        updatedGoal.getCourses().putAll(newCourses);

        when(goalService.addCoursesToGoal(eq(1L), any())).thenReturn(updatedGoal);

        mockMvc.perform(post("/api/goals/1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourses)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.courses", aMapWithSize(5)));

        verify(goalService, times(1)).addCoursesToGoal(eq(1L), any());
    }

    @Test
    void addCoursesToGoal_WhenGoalDoesNotExist_ShouldReturnNotFound() throws Exception {
        Map<Integer, Boolean> newCourses = new HashMap<>();
        newCourses.put(4, false);

        when(goalService.addCoursesToGoal(eq(999L), any())).thenReturn(null);

        mockMvc.perform(post("/api/goals/999/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourses)))
                .andExpect(status().isNotFound());

        verify(goalService, times(1)).addCoursesToGoal(eq(999L), any());
    }

    @Test
    void updateCourseStatus_WhenGoalExistsAndNotAllCompleted_ShouldReturnUpdatedGoal() throws Exception {
        Goal updatedGoal = new Goal();
        updatedGoal.setId(testGoal.getId());

        Map<Integer, Boolean> updatedCourses = new HashMap<>(testGoal.getCourses());
        updatedCourses.put(2, true);  // Change course 2 to completed
        updatedGoal.setCourses(updatedCourses);

        when(goalService.updateCourseStatus(1L, 2, true)).thenReturn(updatedGoal);
        when(goalService.getGoalById(1L)).thenReturn(Optional.of(updatedGoal));

        mockMvc.perform(put("/api/goals/1/courses/2?completed=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.courses.2", is(true)));

        verify(goalService, times(1)).updateCourseStatus(1L, 2, true);
    }

    @Test
    void updateCourseStatus_WhenAllCoursesCompleted_ShouldReturnCompletionMessage() throws Exception {
        when(goalService.updateCourseStatus(1L, 3, true)).thenReturn(null);
        when(goalService.getGoalById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/goals/1/courses/3?completed=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Goal completed and removed"));

        verify(goalService, times(1)).updateCourseStatus(1L, 3, true);
        verify(goalService, times(1)).getGoalById(1L);
    }

    @Test
    void updateCourseStatus_WhenGoalDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(goalService.updateCourseStatus(999L, 1, true)).thenReturn(null);
        when(goalService.getGoalById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/goals/999/courses/1?completed=true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Goal completed and removed"));

        verify(goalService, times(1)).updateCourseStatus(999L, 1, true);
        verify(goalService, times(1)).getGoalById(999L);
    }

    @Test
    void getGoalProgress_ShouldReturnProgressPercentage() throws Exception {
        when(goalService.getGoalProgress(1L)).thenReturn(33.33);

        mockMvc.perform(get("/api/goals/1/progress"))
                .andExpect(status().isOk())
                .andExpect(content().string("33.33"));

        verify(goalService, times(1)).getGoalProgress(1L);
    }

    @Test
    void getGoalsForDashboard_WhenPersonExists_ShouldReturnGoalsWithProgress() throws Exception {
        when(personService.getPersonById(1L)).thenReturn(Optional.of(testPerson));
        when(goalService.getAllGoalsWithProgress(testPerson)).thenReturn(testGoalProgressList);

        mockMvc.perform(get("/api/goals/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Goals retrieved successfully")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].description", is("Learn Spring Boot")))
                .andExpect(jsonPath("$.data[0].progress", closeTo(33.33, 0.01)));

        verify(personService, times(1)).getPersonById(1L);
        verify(goalService, times(1)).getAllGoalsWithProgress(testPerson);
    }

    @Test
    void getGoalsForDashboard_WhenPersonDoesNotExist_ShouldReturnNotFound() throws Exception {
        when(personService.getPersonById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/goals/dashboard/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Person not found with ID: 999")));

        verify(personService, times(1)).getPersonById(999L);
        verify(goalService, never()).getAllGoalsWithProgress(any(Person.class));
    }
}