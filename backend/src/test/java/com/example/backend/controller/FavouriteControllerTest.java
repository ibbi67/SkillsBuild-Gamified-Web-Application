package com.example.backend.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.backend.dao.FavouriteCourseDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.service.FavouriteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.List;
import java.util.Arrays;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class FavouriteControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavouriteService favouriteService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void getFavourite_ShouldReturnFavouriteCourses() throws Exception {
        // Act
        MvcResult result = mockMvc.perform(get("/favourite")
                        .cookie(new javax.servlet.http.Cookie("access_token", "dummy_token")))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        ApiResponse<List<Course>> response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiResponse.class);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void addFavourite_ShouldAddCourseToFavourites() throws Exception {
        // Arrange
        String jsonContent = objectMapper.writeValueAsString(new FavouriteCourseDao(1L));

        // Act
        MvcResult result = mockMvc.perform(post("/favourite/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .cookie(new javax.servlet.http.Cookie("access_token", "dummy_token")))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert
        ApiResponse<Void> response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiResponse.class);
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void removeFavourite_ShouldRemoveCourseFromFavourites() throws Exception {
        // Arrange
        String jsonContent = objectMapper.writeValueAsString(new FavouriteCourseDao(1L));

        // Act
        MvcResult result = mockMvc.perform(delete("/favourite/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .cookie(new javax.servlet.http.Cookie("access_token", "dummy_token")))
                .andExpect(status().isOk())
                .andReturn();

        // Assert
        ApiResponse<Void> response = objectMapper.readValue(result.getResponse().getContentAsString(), ApiResponse.class);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }
}
