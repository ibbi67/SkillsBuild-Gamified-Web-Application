package com.example.backend.controller;

import com.example.backend.dao.SignupDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private ApiResponse<Void> signup(String username, String password) throws Exception {
        String jsonContent = objectMapper.writeValueAsString(new SignupDao(username, password));
        MvcResult result = mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)).andReturn();
        return ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
    }

    private ApiResponse<String> login(String username, String password) throws Exception {
        String jsonContent = objectMapper.writeValueAsString(new SignupDao(username, password));
        MvcResult result = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent)).andReturn();
        Cookie cookie = result.getResponse().getCookie("token");

        if (cookie != null) {
            return ApiResponse.success(result.getResponse().getContentAsString(), cookie.getValue());
        }

        return ApiResponse.deserialise(result.getResponse().getContentAsString(), String.class);
    }

    private ApiResponse<User> me(String token) throws Exception {
        MvcResult result = mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
        return ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);
    }

    @Test
    public void Signup_WithNewUsername_ShouldPass() throws Exception {
        // Act
        ApiResponse<Void> response = signup(testUser.getUsername(), testUser.getPassword());

        // Assert
        // Check if the response status is OK and the response message is correct
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("User created successfully", response.getMessage());

        // Check if the user was created in the database
        User user = userRepository.findByUsername(testUser.getUsername());
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Test
    public void Signup_WithDuplicateUsername_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());

        // Act
        ApiResponse<Void> response = signup(testUser.getUsername(), testUser.getPassword());

        // Assert
        // Check if the response status is BAD_REQUEST and the response message is correct
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        // Check if the response contains the correct message
        assertEquals("User already exists", response.getMessage());
    }

    @Test
    public void Signup_WithEmptyUsername_ShouldFail() throws Exception {
        // Act
        ApiResponse<Void> response = signup("", testUser.getPassword());

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        // Check if the response contains the correct validation message
        assertTrue(response.getMessage().contains("Username cannot be empty"));
    }

    @Test
    public void Signup_WithEmptyPassword_ShouldFail() throws Exception {
        // Act
        ApiResponse<Void> response = signup(testUser.getUsername(), "");

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        // Check if the response contains the correct validation message
        assertTrue(response.getMessage().contains("Password cannot be empty"));
    }

    @Test
    public void Login_WithCorrectCredentials_ShouldPass() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());

        // Act
        ApiResponse<String> response = login(testUser.getUsername(), testUser.getPassword());

        // Assert
        // Check if the response status is OK
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String tokenString = response.getData();

        System.out.println("here here here:" + tokenString);

        // Check if response is not empty
        assertFalse(tokenString.isEmpty());

        // Check if the response is a valid JWT token
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
        ApiResponse<String> response = login(testUser.getUsername(), "wrongpassword");

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        // Check if the response contains the correct message
        assertEquals("Invalid credentials", response.getMessage());
    }

    @Test
    public void Login_WithNonExistentUser_ShouldFail() throws Exception {
        // Act
        ApiResponse<String> response = login("nonexistentuser", "password");

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        // Check if the response contains the correct message
        assertEquals("Invalid credentials", response.getMessage());
    }

    @Test
    public void Login_WithEmptyUsername_ShouldFail() throws Exception {
        // Act
        ApiResponse<String> response = login("", testUser.getPassword());

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        // Check if the response contains the correct validation message
        assertTrue(response.getMessage().contains("Username cannot be empty"));
    }

    @Test
    public void Login_WithEmptyPassword_ShouldFail() throws Exception {
        // Act
        ApiResponse<String> response = login(testUser.getUsername(), "");

        // Assert
        // Check if the response status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        // Check if the response contains the correct validation message
        assertTrue(response.getMessage().contains("Password cannot be empty"));
    }

    @Test
    public void Me_WithValidToken_ShouldPass() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        ApiResponse<String> response = login(testUser.getUsername(), testUser.getPassword());
        String token = response.getData();

        // Act
        ApiResponse<User> meResponse = me(token);

        // Assert
        // Check if the response status is OK
        assertEquals(HttpStatus.OK.value(), meResponse.getStatus());

        // Check if the response contains the correct user details
        assertEquals(testUser.getUsername(), meResponse.getData().getUsername());
    }

    @Test
    public void Me_WithInvalidToken_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        ApiResponse<String> response = login(testUser.getUsername(), testUser.getPassword());
        String token = response.getData() + "invalid";

        // Act
        ApiResponse<User> meResponse = me(token);

        // Assert
        // Check if the response status is UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED.value(), meResponse.getStatus());

        // Check if the response is empty
        assertNull(meResponse.getData());
    }

    @Test
    public void Me_WithEmptyToken_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        login(testUser.getUsername(), testUser.getPassword());
        String token = " ";

        // Act
        ApiResponse<User> response = me(token);

        // Assert
        // Check if the response status is UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        // Check if the response is empty
        assertNull(response.getData());
    }

    @Test
    public void Me_WithTestUserToken_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        login(testUser.getUsername(), testUser.getPassword());
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6InRlc3R1c2VyIiwiaWF0IjoxNTE2MjM5MDIyfQ.3Q2jPCIyjXDkXEgJyUUkm1hsLE0vg_ipi_lJLpNt9_w";

        // Act
        ApiResponse<User> response = me(token);

        // Assert
        // Check if the response status is UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        // Check if the response is empty
        assertNull(response.getData());
    }

    @Test
    public void Me_WithRandomUsernameToken_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        login(testUser.getUsername(), testUser.getPassword());
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IjFsaTI5ZXVudWluanNrZG5jOTAxMmllamtta3ptLHggY21uIHVobzFiMjl1ZWpvbXNkO2NtayIsImlhdCI6MTUxNjIzOTAyMn0.Dw02vCy9IfzsyajfUU35g4g732gqw1CEPMf61VwH6Co";

        // Act
        ApiResponse<User> response = me(token);

        // Assert
        // Check if the response status is UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        // Check if the response is empty
        assertNull(response.getData());
    }

    @Test
    public void Me_WithNullToken_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        login(testUser.getUsername(), testUser.getPassword());

        // Act
        ApiResponse<User> response = me(null);

        // Assert
        // Check if the response status is UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        // Check if the response is empty
        assertNull(response.getData());
    }

    @Test
    public void Me_WithEmptyStringToken_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        login(testUser.getUsername(), testUser.getPassword());

        // Act
        ApiResponse<User> response = me("");

        // Assert
        // Check if the response status is UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        // Check if the response is empty
        assertNull(response.getData());
    }

    @Test
    public void Me_WithBear_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        login(testUser.getUsername(), testUser.getPassword());

        // Act
        MvcResult result = mockMvc.perform(get("/auth/me").header("Authorization", "Bear")
                .contentType(MediaType.APPLICATION_JSON)).andReturn();
        ApiResponse<User> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);

        // Assert
        // Check if the response status is UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        // Check if the response is empty
        assertNull(response.getData());
    }

    @Test
    public void Me_WithNoHeader_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        login(testUser.getUsername(), testUser.getPassword());

        // Act
        MvcResult result = mockMvc.perform(get("/auth/me").contentType(MediaType.APPLICATION_JSON)).andReturn();
        ApiResponse<User> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);

        // Assert
        // Check if the response status is UNAUTHORIZED
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());

        // Check if the response is empty
        assertNull(response.getData());
    }
}
