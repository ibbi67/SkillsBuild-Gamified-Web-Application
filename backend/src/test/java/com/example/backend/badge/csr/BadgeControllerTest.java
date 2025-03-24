package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.badge.BadgeDTO;
import com.example.backend.badge.error.BadgeCreateError;
import com.example.backend.badge.error.BadgeGetError;
import com.example.backend.badge.error.BadgeAwardError;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonRepository;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BadgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private JWT jwt;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    private Badge testBadge;
    private Person testPerson;
    private String validToken;
    private String invalidToken = "invalid-token";

    @BeforeEach
    void setUp() {
        // Clean up existing data
        badgeRepository.deleteAll();
        
        // Create test badge
        testBadge = new Badge(
            "Test Badge",
            "Test Description",
            "/badges/test.png",
            "TEST",
            1
        );
        testBadge = badgeRepository.save(testBadge);

        // Create test person
        testPerson = new Person();
        testPerson.setUsername("testuser");
        testPerson.setPassword("password");
        testPerson.setBadges(new HashSet<>());
        testPerson = personRepository.save(testPerson);

        // Generate valid token
        validToken = jwt.generateAccessToken(testPerson.getUsername());
    }

    @Test
    void getAllBadges_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/badges"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ApiResponse<?> response = objectMapper.readValue(content, ApiResponse.class);

        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void getBadgeById_Success() throws Exception {
        MvcResult result = mockMvc.perform(get("/badges/" + testBadge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ApiResponse<?> response = objectMapper.readValue(content, ApiResponse.class);

        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void getBadgeById_NotFound() throws Exception {
        mockMvc.perform(get("/badges/999"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(BadgeGetError.BADGE_NOT_FOUND.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void createBadge_Success() throws Exception {
        BadgeDTO badgeDTO = new BadgeDTO(
            "New Badge",
            "New Description",
            "/badges/new.png",
            "NEW",
            1
        );

        MvcResult result = mockMvc.perform(post("/badges")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(badgeDTO))
            .cookie(new jakarta.servlet.http.Cookie("accessToken", validToken)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ApiResponse<?> response = objectMapper.readValue(content, ApiResponse.class);

        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void createBadge_Unauthorized() throws Exception {
        BadgeDTO badgeDTO = new BadgeDTO(
            "New Badge",
            "New Description",
            "/badges/new.png",
            "NEW",
            1
        );

        mockMvc.perform(post("/badges")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(badgeDTO))
            .cookie(new jakarta.servlet.http.Cookie("accessToken", invalidToken)))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(BadgeCreateError.UNAUTHORIZED.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void awardBadgeToUser_Success() throws Exception {
        mockMvc.perform(post("/badges/award/" + testPerson.getId() + "/" + testBadge.getId())
            .cookie(new jakarta.servlet.http.Cookie("accessToken", validToken)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("Success"))
            .andExpect(jsonPath("$.data").value("Badge awarded successfully"));

        // Verify the badge was actually awarded
        Person updatedPerson = personRepository.findById(testPerson.getId()).orElseThrow();
        assertTrue(updatedPerson.getBadges().contains(testBadge));
    }

    @Test
    void awardBadgeToUser_Unauthorized() throws Exception {
        mockMvc.perform(post("/badges/award/" + testPerson.getId() + "/" + testBadge.getId())
            .cookie(new jakarta.servlet.http.Cookie("accessToken", invalidToken)))
            .andExpect(status().isForbidden())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(BadgeAwardError.UNAUTHORIZED.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getUserBadges_Success() throws Exception {
        // First award a badge to the user
        testPerson.getBadges().add(testBadge);
        personRepository.save(testPerson);

        MvcResult result = mockMvc.perform(get("/badges/user/" + testPerson.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        String content = result.getResponse().getContentAsString();
        ApiResponse<?> response = objectMapper.readValue(content, ApiResponse.class);

        assertEquals("Success", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void getUserBadges_UserNotFound() throws Exception {
        mockMvc.perform(get("/badges/user/999"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value(BadgeGetError.GET_BADGE_FAILED.getMessage()))
            .andExpect(jsonPath("$.data").isEmpty());
    }
} 