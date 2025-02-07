package com.example.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.backend.dao.LoginDao;
import com.example.backend.dao.SignupDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
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
        MvcResult result = mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
            .andReturn();
        return ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
    }

    private ApiResponse<String> login(String username, String password) throws Exception {
        String jsonContent = objectMapper.writeValueAsString(new LoginDao(username, password));
        MvcResult result = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
            .andReturn();

        int httpStatus = result.getResponse().getStatus();
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<Void> loginResponse = ApiResponse.deserialise(responseBody, Void.class);

        Cookie accessCookie = result.getResponse().getCookie("access_token");

        System.out.println("accessToken: " + accessCookie);

        if (accessCookie != null) {
            String tokenValue = accessCookie.getValue();
            return ApiResponse.success("Login successful", tokenValue);
        }

        return ApiResponse.failed(httpStatus, loginResponse.getMessage());
    }

    private ApiResponse<User> me(String token) throws Exception {
        MvcResult result;
        if (token != null) {
            Cookie cookie = new Cookie("access_token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setDomain("localhost");
            cookie.setMaxAge(86400);

            result = mockMvc
                .perform(get("/auth/me").contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andReturn();
        } else {
            result = mockMvc.perform(get("/auth/me").contentType(MediaType.APPLICATION_JSON)).andReturn();
        }

        String responseBody = result.getResponse().getContentAsString();
        return ApiResponse.deserialise(responseBody, User.class);
    }

    @Test
    public void Signup_WithNewUsername_ShouldPass() throws Exception {
        // Act
        ApiResponse<Void> response = signup(testUser.getUsername(), testUser.getPassword());

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("User created successfully", response.getMessage());

        // Check DB
        User user = userRepository.findByUsername(testUser.getUsername());
        assertNotNull(user);
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Test
    public void Signup_WithDuplicateUsername_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());

        // Act
        ApiResponse<Void> response = signup(testUser.getUsername(), testUser.getPassword());

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("User already exists", response.getMessage());
    }

    @Test
    public void Signup_WithEmptyUsername_ShouldFail() throws Exception {
        // Act
        ApiResponse<Void> response = signup("", testUser.getPassword());

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getMessage().contains("Username cannot be empty"));
    }

    @Test
    public void Signup_WithEmptyPassword_ShouldFail() throws Exception {
        // Act
        ApiResponse<Void> response = signup(testUser.getUsername(), "");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getMessage().contains("Password cannot be empty"));
    }

    @Test
    public void Login_WithCorrectCredentials_ShouldPass() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());

        // Act
        ApiResponse<String> response = login(testUser.getUsername(), testUser.getPassword());

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        String tokenString = response.getData();
        assertNotNull(tokenString);
        assertFalse(tokenString.isEmpty());

        // Make sure the token is valid according to our JwtService
        assertTrue(jwtService.verifyToken(tokenString));

        // The subject (username) of the token should match the user we just signed up
        User extractedUser = jwtService.getUserDetails(tokenString);
        assertNotNull(extractedUser);
        assertEquals(testUser.getUsername(), extractedUser.getUsername());
    }

    @Test
    public void Login_WithIncorrectPassword_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());

        // Act
        ApiResponse<String> response = login(testUser.getUsername(), "wrongpassword");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid credentials", response.getMessage());
    }

    @Test
    public void Login_WithNonExistentUser_ShouldFail() throws Exception {
        // Act
        ApiResponse<String> response = login("nonexistentuser", "password");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid credentials", response.getMessage());
    }

    @Test
    public void Login_WithEmptyUsername_ShouldFail() throws Exception {
        // Act
        ApiResponse<String> response = login("", testUser.getPassword());

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getMessage().contains("Username cannot be empty"));
    }

    @Test
    public void Login_WithEmptyPassword_ShouldFail() throws Exception {
        // Act
        ApiResponse<String> response = login(testUser.getUsername(), "");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getMessage().contains("Password cannot be empty"));
    }

    @Test
    public void Me_WithValidToken_ShouldPass() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        ApiResponse<String> loginResp = login(testUser.getUsername(), testUser.getPassword());
        assertEquals(HttpStatus.OK.value(), loginResp.getStatus());

        String token = loginResp.getData();
        assertTrue(jwtService.verifyToken(token));

        // Act
        ApiResponse<User> meResponse = me(token);

        // Assert
        assertEquals(HttpStatus.OK.value(), meResponse.getStatus());
        assertNotNull(meResponse.getData());
        assertEquals(testUser.getUsername(), meResponse.getData().getUsername());
    }

    @Test
    public void Me_WithInvalidToken_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        ApiResponse<String> loginResp = login(testUser.getUsername(), testUser.getPassword());
        String token = loginResp.getData() + "someInvalidSuffix";

        // Act
        ApiResponse<User> meResponse = me(token);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED.value(), meResponse.getStatus());
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
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNull(response.getData());
    }

    @Test
    public void Me_WithNullToken_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        login(testUser.getUsername(), testUser.getPassword());
        String token = null;

        // Act
        ApiResponse<User> response = me(token);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
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
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNull(response.getData());
    }

    /**
     * Example: trying with an Authorization header or some random token string should fail
     * due to our filter ignoring anything but "access_token" and "refresh_token" cookies.
     */
    @Test
    public void Me_WithNoCookie_ShouldFail() throws Exception {
        // Arrange
        signup(testUser.getUsername(), testUser.getPassword());
        login(testUser.getUsername(), testUser.getPassword());

        // Act
        MvcResult result = mockMvc.perform(get("/auth/me").contentType(MediaType.APPLICATION_JSON)).andReturn();

        ApiResponse<User> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNull(response.getData());
    }
}
