package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.person.PersonDTO;
import com.example.backend.util.DevDataInitializer;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BadgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BadgeRepository badgeRepository;


    private Cookie[] cookies;

    @BeforeEach
    void setUp() throws Exception {
        // Perform signup to get cookies
        PersonDTO personDTO = new PersonDTO("testuser", "password");
        cookies = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookies();
    }

    @Test
    public void testGetAllBadges() throws Exception {
        mockMvc.perform(get("/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Success"))
                .andReturn();
    }

    @Test
    public void testGetBadgeById() throws Exception {
        badgeRepository.save(new Badge("First Favorite", "Added your first course to favorites", "/badges/favorite-1.png", "FAVORITE", 1));
        mockMvc.perform(get("/badges/1")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.message").value("Success"))
                .andReturn();
    }

    @Test
    public void testGetBadgeById_InvalidId() throws Exception {
        mockMvc.perform(get("/badges/-1")
                        .cookie(cookies))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid badge ID"));
    }

    @Test
    public void testGetBadgeById_NotFound() throws Exception {
        mockMvc.perform(get("/badges/999")
                        .cookie(cookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Badge not found"));
    }

    @Test
    public void testGetUserBadges() throws Exception {
        mockMvc.perform(get("/badges/user/1")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.message").value("Success"))
                .andReturn();
    }

    @Test
    public void testGetUserBadges_InvalidToken() throws Exception {
        mockMvc.perform(get("/badges/user/1")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }
}