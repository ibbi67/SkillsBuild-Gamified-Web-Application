package com.example.backend.auth.csr;

import com.example.backend.auth.error.*;
import com.example.backend.person.Person;
import com.example.backend.person.PersonDTO;
import com.example.backend.person.csr.PersonService;
import com.example.backend.badge.csr.BadgeService;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AuthServiceTest {

    private AuthService authService;
    private PersonService personService;
    private JWT jwt;
    private BadgeService badgeService;
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        personService = mock(PersonService.class);
        jwt = mock(JWT.class);
        badgeService = mock(BadgeService.class);
        response = mock(HttpServletResponse.class);
        authService = new AuthService(personService, jwt, badgeService);
    }

    @Test
    public void testSignup() {
        PersonDTO personDTO = new PersonDTO("testUser", "testPass");

        when(personService.findByUsername("testUser")).thenReturn(Optional.empty());
        when(personService.saveNewPerson(personDTO)).thenReturn(Optional.of(new Person()));

        ServiceResult<Void, AuthSignupError> result = authService.signup(personDTO, response);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testLogin() {
        PersonDTO personDTO = new PersonDTO("testUser", "testPass");

        Person person = new Person();
        person.setUsername("testUser");

        when(personService.verifyPassword(personDTO)).thenReturn(Optional.of(person));

        ServiceResult<Void, AuthLoginError> result = authService.login(personDTO, response);
        assertTrue(result.isSuccess());
        verify(jwt).generateRefreshTokenCookie(response, "testUser");
        verify(jwt).generateAccessTokenCookie(response, "testUser");
    }

    @Test
    public void testRefresh() {
        Person person = new Person();
        person.setUsername("testUser");

        when(jwt.getPersonFromToken("validToken")).thenReturn(Optional.of(person));

        ServiceResult<Void, AuthRefreshError> result = authService.refresh("validToken", response);
        assertTrue(result.isSuccess());
        verify(jwt).generateAccessTokenCookie(response, "testUser");
        verify(jwt).generateRefreshTokenCookie(response, "testUser");
    }

    @Test
    public void testLogout() {
        Person person = new Person();
        person.setUsername("testUser");

        when(jwt.getPersonFromToken("validToken")).thenReturn(Optional.of(person));

        ServiceResult<Void, AuthLogoutError> result = authService.logout("validToken", response);
        assertTrue(result.isSuccess());
        verify(jwt).clearCookies(response);
    }

    @Test
    public void testMe() {
        Person person = new Person();
        person.setUsername("testUser");

        when(jwt.getPersonFromToken("validToken")).thenReturn(Optional.of(person));

        ServiceResult<Person, AuthMeError> result = authService.me("validToken");
        assertTrue(result.isSuccess());
        assertEquals("testUser", result.getData().getUsername());
    }

    @Test
    public void testUpdateStreakNewUser() {
        Person person = new Person("newUser", "password");
        authService.updateStreak(person);
        assertEquals(1, person.getStreak());
        assertEquals(LocalDate.now(), person.getLastLoginDate());
    }

    @Test
    public void testUpdateStreakConsecutiveDays() {
        Person person = new Person("consecutiveUser", "password", 2, LocalDate.now().minusDays(1));
        authService.updateStreak(person);
        assertEquals(3, person.getStreak());
        assertEquals(LocalDate.now(), person.getLastLoginDate());
    }

    @Test
    public void testUpdateStreakNonConsecutiveDays() {
        Person person = new Person("nonConsecutiveUser", "password", 2, LocalDate.now().minusDays(3));
        authService.updateStreak(person);
        assertEquals(1, person.getStreak());
        assertEquals(LocalDate.now(), person.getLastLoginDate());
    }

    @Test
    public void testUpdateStreakSameDay() {
        Person person = new Person("sameDayUser", "password", 2, LocalDate.now());
        authService.updateStreak(person);
        assertEquals(2, person.getStreak());
        assertEquals(LocalDate.now(), person.getLastLoginDate());
    }

    @Test
    public void testUpdateStreakFirstLogin() {
        Person person = new Person("firstLoginUser", "password");
        authService.updateStreak(person);
        assertEquals(1, person.getStreak());
        assertEquals(LocalDate.now(), person.getLastLoginDate());
    }

    @Test
    public void testUpdateStreakMultipleDaysMissed() {
        Person person = new Person("missedDaysUser", "password", 3, LocalDate.now().minusDays(5));
        authService.updateStreak(person);
        assertEquals(1, person.getStreak());
        assertEquals(LocalDate.now(), person.getLastLoginDate());
    }

    @Test
    public void testUpdateStreakMultipleLoginsSameDay() {
        Person person = new Person("sameDayUser", "password", 2, LocalDate.now());
        authService.updateStreak(person);
        assertEquals(2, person.getStreak());
        assertEquals(LocalDate.now(), person.getLastLoginDate());
    }

    @Test
    public void testUpdateStreakFailed() {
        Person person = new Person("testUser", "password", 2, LocalDate.now().minusDays(1));
        when(personService.save(person)).thenReturn(Optional.empty());

        ServiceResult<Void, AuthUpdateStreakError> result = authService.updateStreak(person);
        assertFalse(result.isSuccess());
        assertEquals(AuthUpdateStreakError.STREAK_UPDATE_FAILED.getMessage(), result.getError().getMessage());
    }

    @Test
    public void testUpdateStreakSuccess() {
        Person person = new Person("testUser", "password", 2, LocalDate.now().minusDays(1));
        when(personService.save(person)).thenReturn(Optional.of(person));

        ServiceResult<Void, AuthUpdateStreakError> result = authService.updateStreak(person);
        assertTrue(result.isSuccess());
        assertNull(result.getError());
    }
}