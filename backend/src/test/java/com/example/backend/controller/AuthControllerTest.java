package com.example.backend.controller;

import com.example.backend.person.PersonDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSignup() throws Exception {
        PersonDTO personDTO = new PersonDTO("testUser", "testPass");
        String content = objectMapper.writeValueAsString(personDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Signup Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(200, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Success"));
    }

    @Test
    public void testLogin() throws Exception {
        // Perform signup
        PersonDTO personDTO = new PersonDTO("testUser", "testPass");
        String signupContent = objectMapper.writeValueAsString(personDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupContent))
                .andReturn();

        // Perform login
        String loginContent = objectMapper.writeValueAsString(personDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginContent))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Login Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(200, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Success"));
    }

    @Test
    public void testRefresh() throws Exception {
        // Perform signup
        PersonDTO personDTO = new PersonDTO("testUser", "testPass");
        String signupContent = objectMapper.writeValueAsString(personDTO);
        MvcResult signupResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupContent))
                .andReturn();
        Cookie[] cookies = signupResult.getResponse().getCookies();

        // Perform refresh with signup token
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .cookie(cookies))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Refresh Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(200, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Success"));
    }

    @Test
    public void testLogout() throws Exception {
        // Perform signup
        PersonDTO personDTO = new PersonDTO("testUser", "testPass");
        String signupContent = objectMapper.writeValueAsString(personDTO);
        MvcResult signupResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupContent))
                .andReturn();
        Cookie[] cookies = signupResult.getResponse().getCookies();

        // Perform logout with signup token
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                        .cookie(cookies))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Logout Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(200, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Success"));
    }

    @Test
    public void testMe() throws Exception {
        // Perform signup
        PersonDTO personDTO = new PersonDTO("testUser", "testPass");
        String signupContent = objectMapper.writeValueAsString(personDTO);
        MvcResult signupResult = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupContent))
                .andReturn();
        Cookie[] cookies = signupResult.getResponse().getCookies();

        // Perform me with signup token
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/auth/me")
                        .cookie(cookies))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Me Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(200, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("data").get("username").asText().contains("testUser"));
    }

    @Test
    public void testSignupUsernameNullOrEmpty() throws Exception {
        PersonDTO personDTO = new PersonDTO(null, "testPass");
        String content = objectMapper.writeValueAsString(personDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Signup Username Null Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(400, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Username cannot be null or empty"));
    }

    @Test
    public void testSignupPasswordNullOrEmpty() throws Exception {
        PersonDTO personDTO = new PersonDTO("testUser", null);
        String content = objectMapper.writeValueAsString(personDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Signup Password Null Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(400, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Password cannot be null or empty"));
    }

    @Test
    public void testSignupUsernameAlreadyExists() throws Exception {
        PersonDTO personDTO = new PersonDTO("testUser", "testPass");
        String content = objectMapper.writeValueAsString(personDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Signup Username Exists Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(400, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Username already exists"));
    }

    @Test
    public void testLoginUsernameNullOrEmpty() throws Exception {
        PersonDTO personDTO = new PersonDTO(null, "testPass");
        String content = objectMapper.writeValueAsString(personDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Login Username Null Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(400, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Username cannot be null or empty"));
    }

    @Test
    public void testLoginPasswordNullOrEmpty() throws Exception {
        PersonDTO personDTO = new PersonDTO("testUser", null);
        String content = objectMapper.writeValueAsString(personDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Login Password Null Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(400, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Password cannot be null or empty"));
    }

    @Test
    public void testLoginInvalidUsernameOrPassword() throws Exception {
        PersonDTO personDTO = new PersonDTO("invalidUser", "invalidPass");
        String content = objectMapper.writeValueAsString(personDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Login Invalid Credentials Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(401, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Invalid username or password"));
    }

    @Test
    public void testRefreshInvalidToken() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/refresh")
                        .cookie(new Cookie("refreshToken", "invalidToken")))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Refresh Invalid Token Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(401, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Invalid refresh token"));
    }

    @Test
    public void testLogoutInvalidToken() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/logout")
                        .cookie(new Cookie("refreshToken", "invalidToken")))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Logout Invalid Token Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(401, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Invalid refresh token"));
    }

    @Test
    public void testMeInvalidToken() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/auth/me")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Me Invalid Token Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(401, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Invalid access token"));
    }

    @Test
    public void testMeInvalidAccessToken() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/auth/me")
                        .cookie(new Cookie("accessToken", "invalidToken")))
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Me Invalid Token Response Content: " + responseContent);
        JsonNode jsonResponse = objectMapper.readTree(responseContent);
        assertEquals(401, result.getResponse().getStatus());
        assertTrue(jsonResponse.get("message").asText().contains("Invalid access token"));
    }
}
