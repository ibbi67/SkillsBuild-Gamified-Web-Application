package com.example.backend.controller;

import com.example.backend.dao.SignupDao;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private SignupDao testUser;

    @BeforeEach
    public void setup() {
        // Create a unique username for each test to avoid conflicts
        String username = "testuser" + LocalDateTime.now().hashCode();
        testUser = new SignupDao(username, "testpassword");
    }

    private MvcResult signup(String username, String password) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(new SignupDao(username, password));

        return mockMvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(jsonContent))
                .andReturn();
    }

    @Test
    public void whenValidSignupRequestThenReturnOk() throws Exception {
        // Arrange, Act
        MvcResult result = signup(testUser.getUsername(), testUser.getPassword());

        // Assert
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        assertEquals("User created successfully", result.getResponse().getContentAsString());
    }
}
