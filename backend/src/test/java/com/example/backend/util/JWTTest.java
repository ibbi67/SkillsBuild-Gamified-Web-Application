package com.example.backend.util;

import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JWTTest {

    private JWT jwt;
    private PersonService personService;
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        personService = mock(PersonService.class);
        response = mock(HttpServletResponse.class);
        jwt = new JWT(personService);
    }

    @Test
    public void testGenerateAccessToken() {
        String token = jwt.generateAccessToken("testUser");
        assertNotNull(token);
        String username = Jwts.parserBuilder()
                .setSigningKey(jwt.SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        assertEquals("testUser", username);
    }

    @Test
    public void testGenerateRefreshToken() {
        String token = jwt.generateRefreshToken("testUser");
        assertNotNull(token);
        String username = Jwts.parserBuilder()
                .setSigningKey(jwt.SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        assertEquals("testUser", username);
    }

    @Test
    public void testGetPersonFromToken() {
        String token = jwt.generateAccessToken("testUser");
        Person person = new Person();
        person.setUsername("testUser");
        when(personService.findByUsername("testUser")).thenReturn(Optional.of(person));

        Optional<Person> personOptional = jwt.getPersonFromToken(token);
        assertTrue(personOptional.isPresent());
        assertEquals("testUser", personOptional.get().getUsername());
    }

    @Test
    public void testClearCookies() {
        jwt.clearCookies(response);
        verify(response, times(2)).addCookie(any(Cookie.class));
    }

    @Test
    public void testGenerateAccessTokenCookie() {
        jwt.generateAccessTokenCookie(response, "testUser");
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    public void testGenerateRefreshTokenCookie() {
        jwt.generateRefreshTokenCookie(response, "testUser");
        verify(response).addCookie(any(Cookie.class));
    }
}