package com.example.backend.controller;

import com.example.backend.dao.SignupDao;
import com.example.backend.domain.User;
import com.example.backend.domain.ValidationErrorResponse;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
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

import static org.junit.jupiter.api.Assertions.*;
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

    @Autowired
    private JwtService jwtService;

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

    private MvcResult login(String username, String password) throws Exception {
        String jsonContent = objectMapper.writeValueAsString(new SignupDao(username, password));

        return mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
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

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        // Check if the response contains the correct validation message
        ValidationErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                ValidationErrorResponse.class);
        assertTrue(errorResponse.getMessages().contains("Username cannot be empty"));
    }

    @Test
    public void Signup_WithEmptyPassword_ShouldFail() throws Exception {
        // Arrange + Act
        MvcResult result = signup(testUser.getUsername(), "");

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        // Check if the response contains the correct validation message
        ValidationErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                ValidationErrorResponse.class);
        assertTrue(errorResponse.getMessages().contains("Password cannot be empty"));
    }

    @Test
    public void Login_WithCorrectCredentials_ShouldPass() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());

        // Act
        MvcResult result = login(testUser.getUsername(), testUser.getPassword());

        // Assert
        // Check if the response status is OK
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());

        // Check if response is not empty
        assertFalse(result.getResponse().getContentAsString().isEmpty());

        // Check if the response is a valid JWT token
        String tokenString = result.getResponse().getContentAsString();
        assertTrue(jwtService.verifyToken(tokenString));

        // Check if the extracted user from the token is the same as the test user
        User extractedUser = jwtService.getUserDetails(tokenString);
        assertEquals(testUser.getUsername(), extractedUser.getUsername());
    }

    @Test
    public void Login_WithIncorrectPassword_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());

        // Act
        MvcResult result = login(testUser.getUsername(), "wrongpassword");

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        // Check if the response contains the correct message
        assertEquals("Invalid credentials", result.getResponse().getContentAsString());
    }

    @Test
    public void Login_WithNonExistentUser_ShouldFail() throws Exception {
        // Arrange + Act
        MvcResult result = login("nonexistentuser", "password");

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        // Check if the response contains the correct message
        assertEquals("Invalid credentials", result.getResponse().getContentAsString());
    }

    @Test
    public void Login_WithEmptyUsername_ShouldFail() throws Exception {
        // Arrange + Act
        MvcResult result = login("", testUser.getPassword());

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        // Check if the response contains the correct validation message
        ValidationErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                ValidationErrorResponse.class);
        assertTrue(errorResponse.getMessages().contains("Username cannot be empty"));
    }

    @Test
    public void Login_WithEmptyPassword_ShouldFail() throws Exception {
        // Arrange + Act
        MvcResult result = login(testUser.getUsername(), "");

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        // Check if the response contains the correct validation message
        ValidationErrorResponse errorResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                ValidationErrorResponse.class);
        assertTrue(errorResponse.getMessages().contains("Password cannot be empty"));
    }
}
