package com.example.backend.favourite.csr;

import com.example.backend.course.CourseDTO;
import com.example.backend.person.PersonDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FavouriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie[] cookies;

    @BeforeEach
    void setUp() throws Exception {
        // Perform signup to get cookies
        PersonDTO personDTO = new PersonDTO("testuser", "password");
        MvcResult signupResult = mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(personDTO)))
                .andExpect(status().isOk())
                .andReturn();
        cookies = signupResult.getResponse().getCookies();

        CourseDTO courseDTO = new CourseDTO("Example Course", "This is an example course.", "http://example.com", 10, 1);
        mockMvc.perform(post("/courses")
                        .cookie(cookies)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetAllFavourites_ValidRequest() throws Exception {
        // Add a course to favourites first
        mockMvc.perform(post("/favourites/1")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));

        // Now get all favourites
        mockMvc.perform(get("/favourites")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testGetAllFavourites_InvalidToken() throws Exception {
        mockMvc.perform(get("/favourites")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    public void testGetAllFavourites_NoFavouriteCoursesFound() throws Exception {
        mockMvc.perform(get("/favourites")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testAddFavouriteCourse_ValidRequest() throws Exception {
        mockMvc.perform(post("/favourites/1")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    public void testAddFavouriteCourse_InvalidToken() throws Exception {
        mockMvc.perform(post("/favourites/1")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    public void testAddFavouriteCourse_CourseNotFound() throws Exception {
        mockMvc.perform(post("/favourites/999")
                        .cookie(cookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course not found"));
    }

    @Test
    public void testAddFavouriteCourse_CourseAlreadyFavourite() throws Exception {
        mockMvc.perform(post("/favourites/1")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
        mockMvc.perform(post("/favourites/1")
                        .cookie(cookies))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Course is already in favorites"));
    }

    @Test
    public void testRemoveFavouriteCourse_ValidRequest() throws Exception {
        // Add the course to favourites first
        mockMvc.perform(post("/favourites/1")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));

        // Now remove the course from favourites
        mockMvc.perform(delete("/favourites/1")
                        .cookie(cookies))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }

    @Test
    public void testRemoveFavouriteCourse_InvalidToken() throws Exception {
        mockMvc.perform(delete("/favourites/1")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid access token"));
    }

    @Test
    public void testRemoveFavouriteCourse_CourseNotFound() throws Exception {
        mockMvc.perform(delete("/favourites/999")
                        .cookie(cookies))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Course not found"));
    }

    @Test
    public void testRemoveFavouriteCourse_CourseNotFavourite() throws Exception {
        mockMvc.perform(delete("/favourites/1")
                        .cookie(cookies))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Course is not favorite"));
    }
}