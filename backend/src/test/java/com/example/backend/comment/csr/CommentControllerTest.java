package com.example.backend.comment.csr;

import com.example.backend.comment.CommentDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie[] cookies;
    private Integer courseId;

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

        // Create a test course
        CourseDTO courseDTO = new CourseDTO("Test Course", "This is a test course for comments", "http://example.com/course", 10, 3);
        MvcResult courseResult = mockMvc.perform(post("/courses")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract the course ID from the response
        String responseContent = courseResult.getResponse().getContentAsString();
        courseId = objectMapper.readTree(responseContent).get("data").get("id").asInt();
    }

    @Test
    public void testGetCommentsByCourseId_EmptyComments() throws Exception {
        // Test getting comments when none exist
        mockMvc.perform(get("/comments/course/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testAddComment_ValidRequest() throws Exception {
        // Test adding a comment
        CommentDTO commentDTO = new CommentDTO("This is a test comment", courseId);

        mockMvc.perform(post("/comments")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.content").value("This is a test comment"));
    }

    @Test
    public void testGetCommentsByCourseId_WithComments() throws Exception {
        // Add a comment first
        CommentDTO commentDTO = new CommentDTO("This is a test comment", courseId);
        mockMvc.perform(post("/comments")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isCreated());

        // Now get comments for the course
        mockMvc.perform(get("/comments/course/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].content").value("This is a test comment"))
                .andExpect(jsonPath("$.data[0].person.username").value("testuser"));
    }

    @Test
    public void testAddComment_CourseNotFound() throws Exception {
        // Test adding a comment to a non-existent course
        CommentDTO commentDTO = new CommentDTO("This comment should fail", 999);

        mockMvc.perform(post("/comments")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course not found"));
    }



    @Test
    public void testAddComment_InvalidToken() throws Exception {
        // Test adding a comment with an invalid token
        CommentDTO commentDTO = new CommentDTO("This comment should fail", courseId);

        mockMvc.perform(post("/comments")
                        .cookie(new Cookie("accessToken", "invalidToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    public void testGetCommentsByCourseId_CourseNotFound() throws Exception {
        // Test getting comments for a non-existent course
        mockMvc.perform(get("/comments/course/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to get comments"));
    }

    @Test
    public void testAddMultipleComments() throws Exception {
        // Add first comment
        CommentDTO comment1 = new CommentDTO("First test comment", courseId);
        mockMvc.perform(post("/comments")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment1)))
                .andExpect(status().isCreated());

        // Add second comment
        CommentDTO comment2 = new CommentDTO("Second test comment", courseId);
        mockMvc.perform(post("/comments")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment2)))
                .andExpect(status().isCreated());

        // Verify both comments are returned
        mockMvc.perform(get("/comments/course/" + courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].content").exists())
                .andExpect(jsonPath("$.data[1].content").exists());
    }
}