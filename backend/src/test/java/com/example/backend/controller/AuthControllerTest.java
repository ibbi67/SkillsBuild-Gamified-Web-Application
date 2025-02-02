package com.example.backend.controller;

import com.example.backend.dao.SignupDao;
import com.example.backend.domain.User;
import com.example.backend.domain.ValidationErrorResponse;
import com.example.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class AuthControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private SignupDao testUser;

    @BeforeEach
    public void setup() {
        // Create a unique username for each test to avoid conflicts
        String username = "testuser" + LocalDateTime.now().hashCode();
        testUser = new SignupDao(username, "testpassword");
    }

    private MvcResult signup(String username, String password) throws Exception {
        String jsonContent = objectMapper.writeValueAsString(new SignupDao(username, password));

        return mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andReturn();
    }

    @Test
    public void Signup_WithNewUsername_ShouldPass() throws Exception {
        // Arrange + Act
        MvcResult result = signup(testUser.getUsername(), testUser.getPassword());

        // Assert
        // Check if the response status is OK and the response message is correct
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals("User created successfully", result.getResponse().getContentAsString());

        // Check if the user was created in the database
        User user = userRepository.findByUsername(testUser.getUsername());
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Test
    public void Signup_WithDuplicateUsername_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());

        // Check if the user was created in the database
        User user = userRepository.findByUsername(testUser.getUsername());
        assertEquals(testUser.getUsername(), user.getUsername());

        // Act
        MvcResult result = signup(testUser.getUsername(), testUser.getPassword());

        // Assert
        // Check if the response status is BAD_REQUEST and the response message is correct
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        // Check if the response contains the correct message
        assertEquals("User already exists", result.getResponse().getContentAsString());
    }

    @Test
    public void Signup_WithEmptyUsername_ShouldFail() throws Exception {
        // Arrange + Act
        MvcResult result = signup("", testUser.getPassword());
        ValidationErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                ValidationErrorResponse.class);

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        // Check if the response contains the correct validation message
        assertTrue(errorResponse.getMessages().contains("Username cannot be empty"));
    }

    @Test
    public void Signup_WithEmptyPassword_ShouldFail() throws Exception {
        // Arrange + Act
        MvcResult result = signup(testUser.getUsername(), "");
        ValidationErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                ValidationErrorResponse.class);

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        // Check if the response contains the correct validation message
        assertTrue(errorResponse.getMessages().contains("Password cannot be empty"));
    }
}
