package com.example.backend.enrollment.csr;

import com.example.backend.course.CourseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie[] cookies;

    private Cookie[] performSignupAndGetCookies() throws Exception {
        // Perform signup and retrieve cookies
        String signupPayload = "{\"username\":\"testuser\",\"password\":\"password\"}";
        return mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupPayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookies();
    }

    private void addTestCourses() throws Exception {
        CourseDTO course1 = new CourseDTO("Course 1", "Description 1", "http://link1.com", 10, 1);
        CourseDTO course2 = new CourseDTO("Course 2", "Description 2", "http://link2.com", 20, 2);

        mockMvc.perform(post("/courses")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/courses")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course2)))
                .andExpect(status().isCreated());
    }

    private void addTestEnrollments() throws Exception {
        mockMvc.perform(post("/enrollments/{id}", 1).cookie(cookies))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/enrollments/{id}", 2).cookie(cookies))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetAll() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        addTestEnrollments();
        mockMvc.perform(get("/enrollments")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void testGetById() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        addTestEnrollments();
        Integer id = 1;
        mockMvc.perform(get("/enrollments/{id}", id)
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testCreate() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        addTestEnrollments();
        Integer courseId = 1;
        mockMvc.perform(post("/enrollments/{id}", courseId)
                        .cookie(cookies))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void testGetById_InvalidId() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        addTestEnrollments();
        Integer id = -1;
        mockMvc.perform(get("/enrollments/{id}", id)
                        .cookie(cookies))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid ID provided"));
    }

    @Test
    void testGetById_EnrollmentNotFound() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        addTestEnrollments();
        Integer id = 999;
        mockMvc.perform(get("/enrollments/{id}", id)
                        .cookie(cookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Enrollment not found"));
    }

    @Test
    void testCreate_InvalidCourseId() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        addTestEnrollments();
        Integer invalidCourseId = -1;
        mockMvc.perform(post("/enrollments/{id}", invalidCourseId)
                        .cookie(cookies))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid course ID"));
    }

    @Test
    void testCreate_InvalidAccessToken() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        addTestEnrollments();
        Integer courseId = 1;
        mockMvc.perform(post("/enrollments/{id}", courseId)
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    void testCreate_CourseNotFound() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        addTestEnrollments();
        Integer nonExistentCourseId = 999;
        mockMvc.perform(post("/enrollments/{id}", nonExistentCourseId)
                        .cookie(cookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course not found"));
    }

    @Test
    void testGetAll_InvalidAccessToken() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        addTestEnrollments();
        mockMvc.perform(get("/enrollments")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    void testGetAll_NoEnrollmentsFound() throws Exception {
        cookies = performSignupAndGetCookies();
        addTestCourses();
        // Assuming a valid token but no enrollments exist for the user
        mockMvc.perform(get("/enrollments")
                        .cookie(cookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Enrollment not found"));
    }
}