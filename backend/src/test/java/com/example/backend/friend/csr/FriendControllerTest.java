package com.example.backend.friend.csr;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Cookie[] cookies;

    @BeforeEach
    void setUp() throws Exception {
        // Perform signup to get cookies
        String signupPayload = "{\"username\":\"testuser\",\"password\":\"password\"}";
        cookies = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupPayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookies();
        signupPayload = "{\"username\":\"testuser-2\",\"password\":\"password\"}";
        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupPayload))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookies();
    }

    @Test
    void getAllFriends_whenSuccessful_returnsOkResponse() throws Exception {
        mockMvc.perform(get("/friends")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void getAllFriends_whenInvalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/friends")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    void addFriend_whenSuccessful_returnsOkResponse() throws Exception {
        String payload = "{\"personId\":\"2\"}";
        mockMvc.perform(post("/friends")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void addFriend_whenInvalidToken_returnsUnauthorized() throws Exception {
        String payload = "{\"username\":\"frienduser\"}";
        mockMvc.perform(post("/friends")
                        .cookie(new Cookie("accessToken", "invalidToken"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    void removeFriend_whenSuccessful_returnsOkResponse() throws Exception {
        String payload = "{\"personId\":\"2\"}";
        mockMvc.perform(post("/friends")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/friends/2")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    void addFriend_whenPersonNotFound_returnsNotFound() throws Exception {
        String payload = "{\"personId\":\"999\"}";
        mockMvc.perform(post("/friends")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void addFriend_whenAlreadyFriends_returnsBadRequest() throws Exception {
        String payload = "{\"personId\":\"2\"}";
        mockMvc.perform(post("/friends")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());
        mockMvc.perform(post("/friends")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Already friends"));
    }

    @Test
    void addFriend_whenCannotAddSelf_returnsBadRequest() throws Exception {
        String payload = "{\"personId\":\"1\"}";
        mockMvc.perform(post("/friends")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot add yourself as a friend"));
    }

    @Test
    void addFriend_whenInternalError_returnsInternalServerError() throws Exception {
        String payload = "{\"personId\":\"500\"}";
        mockMvc.perform(post("/friends")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void removeFriend_whenPersonNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/friends/999")
                        .cookie(cookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void removeFriend_whenNotFriends_returnsBadRequest() throws Exception {
        mockMvc.perform(delete("/friends/2")
                        .cookie(cookies))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You are not friends with this user"));
    }

    @Test
    void removeFriend_whenInternalError_returnsInternalServerError() throws Exception {
        mockMvc.perform(delete("/friends/500")
                        .cookie(cookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void getAllFriends_whenNoFriendsFound_returnsNotFound() throws Exception {
        mockMvc.perform(get("/friends")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }
}