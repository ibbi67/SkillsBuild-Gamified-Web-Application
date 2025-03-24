package com.example.backend.leaderboard;

import com.example.backend.course.CourseDTO;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetLeaderboard() throws Exception {
        // Perform signup
        PersonDTO personDTO = new PersonDTO("testUser", "testPass");
        String signupContent = objectMapper.writeValueAsString(personDTO);
        Cookie[] cookies = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupContent))
                .andExpect(status().isOk()).andReturn()
                .getResponse().getCookies();

        // Add courses
        CourseDTO course1 = new CourseDTO("Course 1", "Description 1", "http://link1.com", 10, 1);
        CourseDTO course2 = new CourseDTO("Course 2", "Description 2", "http://link2.com", 20, 2);
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course1)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course2)))
                .andExpect(status().isCreated());

        // Enroll into courses
        mockMvc.perform(post("/enrollments/{id}", 1).cookie(cookies))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/enrollments/{id}", 2).cookie(cookies))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(get("/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].username", is("testUser")))
                .andExpect(jsonPath("$.data[0].score", is(20)))
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Get Leaderboard Response Content: " + jsonResponse);
    }

    @Test
    public void testGetLeaderboardNotFound() throws Exception {
        mockMvc.perform(get("/leaderboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", is("Leaderboard not found")));
    }
}
