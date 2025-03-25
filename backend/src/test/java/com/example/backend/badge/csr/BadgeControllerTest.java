package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.badge.BadgeDTO;
import com.example.backend.badge.error.BadgeAwardError;
import com.example.backend.badge.error.BadgeCreateError;
import com.example.backend.badge.error.BadgeGetError;
import com.example.backend.person.PersonDTO;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private int testUserId;
    private Cookie emptyToken;
    private int badgeId;
    private Cookie[] adminCookies;

    @BeforeEach
    public void setUp() throws Exception {
        // Create a test user and get their tokens
        PersonDTO testUser = new PersonDTO("testuser", "password");
        MvcResult signupResult = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract tokens from cookies
        cookies = signupResult.getResponse().getCookies();
        emptyToken = new Cookie("accessToken", "");

        // Create a test badge
        BadgeDTO testBadge = new BadgeDTO("New Test Badge", "Description for New Test Badge",
                "/badges/new-test-badge.png", "TEST", 1);
        mockMvc.perform(post("/badges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBadge))
                .cookie(cookies))
                .andExpect(status().isCreated())
                .andReturn();

        // Get the test user's ID
        MvcResult meResult = mockMvc.perform(get("/auth/me")
                .cookie(cookies))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode userNode = objectMapper.readTree(meResult.getResponse().getContentAsString())
                .get("data");
        testUserId = userNode.get("id").asInt();

        // Get the test badge's ID
        MvcResult badgesResult = mockMvc.perform(get("/badges"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode badgesArray = objectMapper.readTree(badgesResult.getResponse().getContentAsString())
                .get("data");
        badgeId = badgesArray.get(0).get("id").asInt();

        // Create an admin user and get their tokens
        PersonDTO adminUser = new PersonDTO("admin", "adminpass");
        MvcResult adminSignupResult = mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminUser)))
                .andExpect(status().isOk())
                .andReturn();

        adminCookies = adminSignupResult.getResponse().getCookies();
    }

    @Test
    @Transactional
    public void testGetAllBadges() throws Exception {
        // Test successful retrieval
        mockMvc.perform(get("/badges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").isArray());

        // Test when no badges exist
        badgeRepository.deleteAll();
        mockMvc.perform(get("/badges"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(BadgeGetError.BADGE_NOT_FOUND.getMessage()));
    }

    @Test
    @Transactional
    public void testGetBadgeById() throws Exception {
        // Test successful retrieval
        mockMvc.perform(get("/badges/" + badgeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.id").value(badgeId));

        // Test non-existent badge
        mockMvc.perform(get("/badges/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(BadgeGetError.BADGE_NOT_FOUND.getMessage()));

        // Test invalid ID format
        mockMvc.perform(get("/badges/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value 'abc' for parameter 'id'. Expected type: Integer"));
    }

    @Test
    @Transactional
    public void testGetBadgesByCriteriaType() throws Exception {
        // Test successful retrieval
        mockMvc.perform(get("/badges/type/TEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data[0].criteriaType").value("TEST"));

        // Test non-existent criteria type
        mockMvc.perform(get("/badges/type/NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(BadgeGetError.BADGE_NOT_FOUND.getMessage()));

        // Test empty criteria type
        mockMvc.perform(get("/badges/type/"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void testGetUserBadges() throws Exception {
        // Test successful retrieval
        mockMvc.perform(get("/badges/user/" + testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").isArray());

        // Test non-existent user
        mockMvc.perform(get("/badges/user/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(BadgeGetError.GET_BADGE_FAILED.getMessage()));

        // Test invalid user ID
        mockMvc.perform(get("/badges/user/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(BadgeGetError.GET_BADGE_FAILED.getMessage()));
    }

    @Test
    @Transactional
    public void testCreateBadge() throws Exception {
        // Test successful creation
        BadgeDTO validBadge = new BadgeDTO("Valid Badge", "Valid Description", 
                "/badges/valid.png", "VALID", 1);
        mockMvc.perform(post("/badges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBadge))
                .cookie(cookies))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Success"));

        // Test unauthorized creation
        mockMvc.perform(post("/badges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validBadge))
                .cookie(emptyToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(BadgeCreateError.UNAUTHORIZED.getMessage()));

        // Test invalid badge data
        BadgeDTO invalidBadge = new BadgeDTO(null, null, null, null, null);
        mockMvc.perform(post("/badges")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBadge))
                .cookie(cookies))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(BadgeCreateError.BADGE_CREATION_FAILED.getMessage()));
    }

    @Test
    @Transactional
    public void testAwardBadgeToUser() throws Exception {
        // Test successful award
        mockMvc.perform(post("/badges/award/" + testUserId + "/" + badgeId)
                .cookie(adminCookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));

        // Test unauthorized award
        mockMvc.perform(post("/badges/award/" + testUserId + "/" + badgeId)
                .cookie(emptyToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(BadgeAwardError.UNAUTHORIZED.getMessage()));

        // Test non-existent badge
        mockMvc.perform(post("/badges/award/" + testUserId + "/999")
                .cookie(adminCookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(BadgeAwardError.BADGE_NOT_FOUND.getMessage()));

        // Test non-existent user
        mockMvc.perform(post("/badges/award/999/" + badgeId)
                .cookie(adminCookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(BadgeAwardError.USER_NOT_FOUND.getMessage()));

        // Test invalid parameters
        mockMvc.perform(post("/badges/award/invalid/" + badgeId)
                .cookie(adminCookies))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value 'invalid' for parameter 'userId'. Expected type: Integer"));
    }

    @Test
    @Transactional
    public void testEdgeCases() throws Exception {
        // Test missing auth token
        mockMvc.perform(post("/badges/award/1/1"))
                .andExpect(status().isBadRequest());

        // Test malformed JSON
        mockMvc.perform(post("/badges")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"malformed\": \"json\"")
                .cookie(cookies))
                .andExpect(status().isBadRequest());

        // Test invalid token format
        Cookie invalidToken = new Cookie("accessToken", "definitely.not.jwt");
        mockMvc.perform(post("/badges/award/1/1")
                .cookie(invalidToken))
                .andExpect(status().isForbidden());
    }
}
