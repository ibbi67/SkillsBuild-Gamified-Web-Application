package com.example.backend.streak;

import com.example.backend.person.PersonDTO;
import com.example.backend.util.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StreakControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie[] cookies;

    @BeforeEach
    void setUp() throws Exception {
        cookies = performSignupAndGetCookies();
    }

    private Cookie[] performSignupAndGetCookies() throws Exception {
        PersonDTO personDTO = new PersonDTO("testuser", "password");
        MvcResult signupResult = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andReturn();
        return signupResult.getResponse().getCookies();
    }

    @Test
    void getStreak_validToken_returnsStreak() throws Exception {
        MvcResult result = mockMvc.perform(get("/streak")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        ApiResponse<Integer> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertEquals("Success", response.getMessage());
        assertEquals(1, response.getData());
    }

    @Test
    void getStreak_invalidAccessToken_returnsUnauthorized() throws Exception {
        MvcResult result = mockMvc.perform(get("/streak")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        ApiResponse<Integer> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertEquals("Invalid access token", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void getStreak_invalidToken_returnsUnauthorized() throws Exception {
        MvcResult result = mockMvc.perform(get("/streak")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andReturn();
        String jsonResponse = result.getResponse().getContentAsString();
        ApiResponse<Integer> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {
        });
        assertEquals("Invalid access token", response.getMessage());
        assertNull(response.getData());
    }
}
