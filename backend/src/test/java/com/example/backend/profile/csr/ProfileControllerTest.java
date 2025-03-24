package com.example.backend.profile.csr;

import com.example.backend.profile.ProfileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie[] cookies;

    @BeforeEach
    void setUp() throws Exception {
        // Perform signup to get cookies
        ProfileDTO profileDTO = new ProfileDTO("username", "password", "firstName", "lastName", "email", "avatarLink");
        MvcResult signupResult = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDTO)))
                .andExpect(status().isOk())
                .andReturn();
        cookies = signupResult.getResponse().getCookies();
    }

    @Test
    void testUpdate_Success() throws Exception {
        ProfileDTO profileDTO = new ProfileDTO("username", "password", "newFirstName", "newLastName", "newEmail", "newAvatarLink");
        mockMvc.perform(put("/profile")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void testUpdate_InvalidAccessToken() throws Exception {
        ProfileDTO profileDTO = new ProfileDTO("username", "password", "newFirstName", "newLastName", "newEmail", "newAvatarLink");
        mockMvc.perform(put("/profile")
                        .cookie(new Cookie("accessToken", "invalidToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }
}