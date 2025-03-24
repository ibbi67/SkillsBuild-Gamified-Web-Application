package com.example.backend.streak;

import com.example.backend.person.Person;
import com.example.backend.streak.csr.StreakService;
import com.example.backend.streak.error.StreakGetError;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StreakServiceTest {

    @Mock
    private JWT jwt;

    @InjectMocks
    private StreakService streakService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getStreak_validToken_returnsStreak() {
        String refreshToken = "validToken";
        Person person = mock(Person.class);
        when(jwt.getPersonFromToken(refreshToken)).thenReturn(Optional.of(person));
        when(person.getStreak()).thenReturn(5);

        ServiceResult<Integer, StreakGetError> result = streakService.getStreak(refreshToken);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getData());
    }

    @Test
    void getStreak_invalidToken_returnsError() {
        String refreshToken = "invalidToken";
        when(jwt.getPersonFromToken(refreshToken)).thenReturn(Optional.empty());

        ServiceResult<Integer, StreakGetError> result = streakService.getStreak(refreshToken);

        assertFalse(result.isSuccess());
        assertEquals(StreakGetError.INVALID_ACCESS_TOKEN, result.getError());
    }
}
