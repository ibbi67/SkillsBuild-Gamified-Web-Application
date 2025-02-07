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

    private User testUser = new User();

    @BeforeEach
    public void setup() {
        testUser.setUsername("testuser" + LocalDateTime.now().hashCode());
        testUser.setPassword("testpassword");
    }

    @Test
    public void Signup_WithNewUsername_ShouldPass() throws Exception {
        // Arrange
        String jsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
            .andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("User created successfully", response.getMessage());

        User user = userRepository.findByUsername(testUser.getUsername());
        assertNotNull(user);
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Test
    public void Signup_WithDuplicateUsername_ShouldFail() throws Exception {
        // Arrange
        String jsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(jsonContent)).andReturn();

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
            .andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("User already exists", response.getMessage());
    }

    @Test
    public void Signup_WithEmptyUsername_ShouldFail() throws Exception {
        // Arrange
        String jsonContent = objectMapper.writeValueAsString(new SignupDao("", testUser.getPassword()));

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
            .andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getMessage().contains("Username cannot be empty"));
    }

    @Test
    public void Signup_WithEmptyPassword_ShouldFail() throws Exception {
        // Arrange
        String jsonContent = objectMapper.writeValueAsString(new SignupDao(testUser.getUsername(), ""));

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
            .andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getMessage().contains("Password cannot be empty"));
    }

    @Test
    public void Login_WithCorrectCredentials_ShouldPass() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Assert
        int httpStatus = result.getResponse().getStatus();
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<Void> loginResponse = ApiResponse.deserialise(responseBody, Void.class);

        Cookie accessCookie = result.getResponse().getCookie("access_token");

        assertEquals(HttpStatus.OK.value(), httpStatus);
        assertEquals("Login successful", loginResponse.getMessage());
        assertNotNull(accessCookie);
        assertFalse(accessCookie.getValue().isEmpty());

        assertTrue(jwtService.verifyToken(accessCookie.getValue()));

        User extractedUser = jwtService.getUserDetails(accessCookie.getValue());
        assertNotNull(extractedUser);
        assertEquals(testUser.getUsername(), extractedUser.getUsername());
    }

    @Test
    public void Login_WithIncorrectPassword_ShouldFail() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), "wrongpassword")
        );

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid credentials", response.getMessage());
    }

    @Test
    public void Login_WithNonExistentUser_ShouldFail() throws Exception {
        // Arrange
        String loginJsonContent = objectMapper.writeValueAsString(new LoginDao("nonexistentuser", "password"));

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid credentials", response.getMessage());
    }

    @Test
    public void Login_WithEmptyUsername_ShouldFail() throws Exception {
        // Arrange
        String loginJsonContent = objectMapper.writeValueAsString(new LoginDao("", testUser.getPassword()));

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getMessage().contains("Username cannot be empty"));
    }

    @Test
    public void Login_WithEmptyPassword_ShouldFail() throws Exception {
        // Arrange
        String loginJsonContent = objectMapper.writeValueAsString(new LoginDao(testUser.getUsername(), ""));

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue(response.getMessage().contains("Password cannot be empty"));
    }

    @Test
    public void Me_WithValidToken_ShouldPass() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        MvcResult loginResult = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        Cookie accessCookie = loginResult.getResponse().getCookie("access_token");
        assertNotNull(accessCookie);
        assertTrue(jwtService.verifyToken(accessCookie.getValue()));

        // Act
        MvcResult result = mockMvc
            .perform(get("/auth/me").contentType(MediaType.APPLICATION_JSON).cookie(accessCookie))
            .andReturn();

        // Assert
        ApiResponse<User> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getData());
        assertEquals(testUser.getUsername(), response.getData().getUsername());
    }

    @Test
    public void Me_WithInvalidToken_ShouldFail() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        MvcResult loginResult = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        Cookie accessCookie = loginResult.getResponse().getCookie("access_token");
        assertNotNull(accessCookie);
        String invalidToken = accessCookie.getValue() + "someInvalidSuffix";

        // Act
        MvcResult result = mockMvc
            .perform(
                get("/auth/me").contentType(MediaType.APPLICATION_JSON).cookie(new Cookie("access_token", invalidToken))
            )
            .andReturn();

        // Assert
        ApiResponse<User> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNull(response.getData());
    }

    @Test
    public void Me_WithEmptyToken_ShouldFail() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Act
        MvcResult result = mockMvc
            .perform(get("/auth/me").contentType(MediaType.APPLICATION_JSON).cookie(new Cookie("access_token", " ")))
            .andReturn();

        // Assert
        ApiResponse<User> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNull(response.getData());
    }

    @Test
    public void Me_WithNullToken_ShouldFail() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Act
        MvcResult result = mockMvc.perform(get("/auth/me").contentType(MediaType.APPLICATION_JSON)).andReturn();

        // Assert
        ApiResponse<User> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNull(response.getData());
    }

    @Test
    public void Me_WithEmptyStringToken_ShouldFail() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Act
        MvcResult result = mockMvc
            .perform(get("/auth/me").contentType(MediaType.APPLICATION_JSON).cookie(new Cookie("access_token", "")))
            .andReturn();

        // Assert
        ApiResponse<User> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNull(response.getData());
    }

    @Test
    public void Me_WithNoCookie_ShouldFail() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Act
        MvcResult result = mockMvc.perform(get("/auth/me").contentType(MediaType.APPLICATION_JSON)).andReturn();

        // Assert
        ApiResponse<User> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), User.class);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertNull(response.getData());
    }

    @Test
    public void Refresh_WithValidRefreshToken_ShouldPass() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        MvcResult loginResult = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        Cookie refreshCookie = loginResult.getResponse().getCookie("refresh_token");
        assertNotNull(refreshCookie);
        assertTrue(jwtService.verifyToken(refreshCookie.getValue()));

        // Act
        MvcResult result = mockMvc.perform(post("/auth/refresh").cookie(refreshCookie)).andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Token refreshed", response.getMessage());
    }

    @Test
    public void Refresh_WithInvalidRefreshToken_ShouldFail() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        MvcResult loginResult = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        Cookie refreshCookie = loginResult.getResponse().getCookie("refresh_token");
        assertNotNull(refreshCookie);
        String invalidToken = refreshCookie.getValue() + "someInvalidSuffix";

        // Act
        MvcResult result = mockMvc
            .perform(post("/auth/refresh").cookie(new Cookie("refresh_token", invalidToken)))
            .andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid refresh token", response.getMessage());
    }

    @Test
    public void Refresh_WithNoRefreshToken_ShouldFail() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        // Act
        MvcResult result = mockMvc.perform(post("/auth/refresh")).andReturn();

        System.out.println("here here here" + result.getResponse().getContentAsString());

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Invalid refresh token", response.getMessage());
    }

    @Test
    public void Logout_ShouldPass() throws Exception {
        // Arrange
        String signupJsonContent = objectMapper.writeValueAsString(
            new SignupDao(testUser.getUsername(), testUser.getPassword())
        );
        mockMvc
            .perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJsonContent))
            .andReturn();

        String loginJsonContent = objectMapper.writeValueAsString(
            new LoginDao(testUser.getUsername(), testUser.getPassword())
        );
        MvcResult loginResult = mockMvc
            .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJsonContent))
            .andReturn();

        Cookie accessCookie = loginResult.getResponse().getCookie("access_token");
        Cookie refreshCookie = loginResult.getResponse().getCookie("refresh_token");

        // Act
        MvcResult result = mockMvc.perform(post("/auth/logout").cookie(accessCookie, refreshCookie)).andReturn();

        // Assert
        ApiResponse<Void> response = ApiResponse.deserialise(result.getResponse().getContentAsString(), Void.class);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Logout successful", response.getMessage());
    }
}
