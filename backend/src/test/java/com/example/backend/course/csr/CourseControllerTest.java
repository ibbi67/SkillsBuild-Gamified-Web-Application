package com.example.backend.course.csr;

import com.example.backend.course.CourseDTO;
import com.example.backend.person.PersonDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie[] cookies;

    @BeforeEach
    void setUp() throws Exception {
        // Perform signup to get cookies
        PersonDTO personDTO = new PersonDTO("testuser", "password");
        MvcResult signupResult = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andReturn();
        cookies = signupResult.getResponse().getCookies();
    }

    @Test
    public void testCreateCourse() throws Exception {
        CourseDTO courseDTO = new CourseDTO("Course 1", "Description 1", "http://link1.com", 10, 1);
        mockMvc.perform(post("/courses")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Course 1"));
    }

    @Test
    public void testGetCourseById() throws Exception {
        // Create a course first
        CourseDTO courseDTO = new CourseDTO("Course 1", "Description 1", "http://link1.com", 10, 1);
        MvcResult result = mockMvc.perform(post("/courses")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        int courseId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asInt();

        // Get the course by ID
        mockMvc.perform(get("/courses/" + courseId)
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Course 1"));
    }

    @Test
    public void testGetAllCourses() throws Exception {
        // Create a course first
        CourseDTO courseDTO = new CourseDTO("Course 1", "Description 1", "http://link1.com", 10, 1);
        mockMvc.perform(post("/courses")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated());

        // Get all courses
        MvcResult result = mockMvc.perform(get("/courses")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);

        mockMvc.perform(get("/courses")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("Course 1"));
    }

    @Test
    public void testGetRecommendedCourses() throws Exception {
        // Create a course first
        CourseDTO courseDTO = new CourseDTO("Course 1", "Description 1", "http://link1.com", 10, 1);
        CourseDTO courseDTO2 = new CourseDTO("Course 2", "Description 2", "http://link2.com", 20, 1);
        mockMvc.perform(post("/courses")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/courses")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/favourites/{id}", 1)
                        .cookie(cookies))
                .andExpect(status().isOk());

        // Get recommended courses
        mockMvc.perform(get("/courses/recommend")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("Course 2"));
    }

    @Test
    public void testGetCourseById_NotFound() throws Exception {
        // Simulate course not found
        mockMvc.perform(get("/courses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course not found"));
    }

    @Test
    public void testCreateCourse_Failure() throws Exception {
        CourseDTO courseDTO = new CourseDTO("Course 1", "Description 1", "http://link1.com", 10, 1);
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to create course"));
    }

    @Test
    public void testGetRecommendedCourses_InvalidAccessToken() throws Exception {
        mockMvc.perform(get("/courses/recommend")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }
}
