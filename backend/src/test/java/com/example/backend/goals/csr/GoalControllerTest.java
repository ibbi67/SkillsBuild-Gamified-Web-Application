package com.example.backend.goals.csr;

import com.example.backend.course.CourseDTO;
import com.example.backend.goals.AddEnrollmentDTO;
import com.example.backend.goals.GoalDTO;
import com.example.backend.person.PersonDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GoalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie[] cookies;

    private Cookie[] performSignupAndGetCookies() throws Exception {
        PersonDTO personDTO = new PersonDTO("testUser", "testPassword");
        MvcResult result = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getCookies();
    }

    private void addTestCourses() throws Exception {
        // Add two test courses for the goal tests
        CourseDTO course1 = new CourseDTO("Java Programming", "Learn Java Programming", "https://example.com/java", 10, 15);
        CourseDTO course2 = new CourseDTO("Spring Framework", "Learn Sprign Framework", "https://example.com/spring", 10, 15);

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course2)))
                .andExpect(status().isCreated());
    }

    private void createEnrollments() throws Exception {
        // Enroll in both courses
        mockMvc.perform(post("/enrollments/1")
                .cookie(cookies))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/enrollments/2")
                .cookie(cookies))
                .andExpect(status().isCreated());
    }

    private void createTestGoal() throws Exception {
        GoalDTO goalDTO = new GoalDTO(LocalDate.now(), LocalDate.now().plusDays(30), "Complete Java Course", "Java Certificate");

        mockMvc.perform(post("/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goalDTO))
                .cookie(cookies))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateGoal_ValidRequest() throws Exception {
        cookies = performSignupAndGetCookies();

        GoalDTO goalDTO = new GoalDTO(LocalDate.now(), LocalDate.now().plusDays(30), "Complete Java Course", "Java Certificate");

        mockMvc.perform(post("/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goalDTO))
                .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testCreateGoal_InvalidAccessToken() throws Exception {
        GoalDTO goalDTO = new GoalDTO(LocalDate.now(), LocalDate.now().plusDays(30), "Complete Java Course", "Java Certificate");

        mockMvc.perform(post("/goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(goalDTO))
                .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    void testDeleteGoal_ValidRequest() throws Exception {
        cookies = performSignupAndGetCookies();
        createTestGoal();

        // Get the goal ID (assuming it's 1 since it's the first goal)
        Long goalId = 1L;

        mockMvc.perform(delete("/goals/{goalId}", goalId)
                .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testDeleteGoal_InvalidAccessToken() throws Exception {
        Long goalId = 1L;

        mockMvc.perform(delete("/goals/{goalId}", goalId)
                .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    void testDeleteGoal_GoalNotFound() throws Exception {
        cookies = performSignupAndGetCookies();
        Long nonExistentGoalId = 999L;

        mockMvc.perform(delete("/goals/{goalId}", nonExistentGoalId)
                .cookie(cookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Goal not found"));
    }

    @Test
    void testAddEnrollmentToGoal() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        createEnrollments();
        createTestGoal();

        Long goalId = 1L;
        AddEnrollmentDTO addEnrollmentDTO = new AddEnrollmentDTO(Arrays.asList(1, 2));

        mockMvc.perform(post("/goals/{goalId}", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addEnrollmentDTO))
                .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testAddEnrollmentToGoal_GoalNotFound() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        createEnrollments();

        Long nonExistentGoalId = 999L;
        AddEnrollmentDTO addEnrollmentDTO = new AddEnrollmentDTO(Arrays.asList(1, 2));


        mockMvc.perform(post("/goals/{goalId}", nonExistentGoalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addEnrollmentDTO))
                .cookie(cookies))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Goal not found"));
    }

    @Test
    void testAddEnrollmentToGoal_EnrollmentsNotFound() throws Exception {
        cookies = performSignupAndGetCookies();
        createTestGoal();

        Long goalId = 1L;
        AddEnrollmentDTO addEnrollmentDTO = new AddEnrollmentDTO(List.of(999));

        mockMvc.perform(post("/goals/{goalId}", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addEnrollmentDTO))
                .cookie(cookies))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Enrollments not found"));
    }

    @Test
    void testUpdateEnrollmentCompletionStatus() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        createEnrollments();
        createTestGoal();

        // Add enrollments to the goal
        Long goalId = 1L;
        AddEnrollmentDTO addEnrollmentDTO = new AddEnrollmentDTO(Arrays.asList(1, 2));

        mockMvc.perform(post("/goals/{goalId}", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addEnrollmentDTO))
                .cookie(cookies))
                .andExpect(status().isOk());

        // Update enrollment completion status
        Integer enrollmentId = 1;

        mockMvc.perform(put("/goals/{goalId}/enrollments/{enrollmentId}", goalId, enrollmentId)
                .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdateEnrollmentCompletionStatus_InvalidAccessToken() throws Exception {
        Long goalId = 1L;
        Integer enrollmentId = 1;

        mockMvc.perform(put("/goals/{goalId}/enrollments/{enrollmentId}", goalId, enrollmentId)
                .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    void testUpdateEnrollmentCompletionStatus_GoalNotFound() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        createEnrollments();

        Long nonExistentGoalId = 999L;
        Integer enrollmentId = 1;

        mockMvc.perform(put("/goals/{goalId}/enrollments/{enrollmentId}", nonExistentGoalId, enrollmentId)
                .cookie(cookies))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Goal not found"));
    }

    @Test
    void testUpdateEnrollmentCompletionStatus_EnrollmentNotFound() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        createEnrollments();
        createTestGoal();

        // Add enrollments to the goal
        Long goalId = 1L;
        AddEnrollmentDTO addEnrollmentDTO = new AddEnrollmentDTO(Arrays.asList(1, 2));

        mockMvc.perform(post("/goals/{goalId}", goalId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addEnrollmentDTO))
                .cookie(cookies))
                .andExpect(status().isOk());

        // Try to update a non-existent enrollment
        Integer nonExistentEnrollmentId = 999;

        mockMvc.perform(put("/goals/{goalId}/enrollments/{enrollmentId}", goalId, nonExistentEnrollmentId)
                .cookie(cookies))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Enrollments not found"));
    }

    @Test
    void testGetGoals() throws Exception {
        cookies = performSignupAndGetCookies();
        createTestGoal();

        mockMvc.perform(get("/goals")
                .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void testGetGoals_InvalidAccessToken() throws Exception {
        mockMvc.perform(get("/goals")
                .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }
}