package com.example.backend.service;

import com.example.backend.domain.Streak;
import com.example.backend.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StreakTest {

    @Mock
    private UserService userService;

    @Mock
    private StreaksService streaksService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Streak streak;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        streak = new Streak();
        user.setStreak(streak);
        streak.setUser(user);
    }

    @Test
    void checkAndUpdateStreak_withFirstTimeLogin_shouldInitializeToOne() {
        // Arrange
        streak.setPreviousLogin(null);
        streak.setStreak(0);

        // Act
        authService.checkAndUpdateStreak(user);

        // Assert
        assertEquals(1, streak.getStreak());
        verify(streaksService).saveStreak(streak);
    }

    @Test
    void checkAndUpdateStreak_withSameDayLogin_shouldNotUpdate() {
        // Arrange
        LocalDate today = LocalDate.now();
        Date todayDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        streak.setPreviousLogin(todayDate);
        int currentStreak = 5;
        streak.setStreak(currentStreak);

        // Act
        authService.checkAndUpdateStreak(user);

        // Assert
        assertEquals(currentStreak, streak.getStreak());
        verify(userService, never()).save(any());
        verify(streaksService, never()).saveStreak(any());
    }

    @Test
    void checkAndUpdateStreak_withConsecutiveDayLogin_shouldIncrement() {
        // Arrange
        LocalDate yesterday = LocalDate.now().minusDays(1);
        Date yesterdayDate = Date.from(yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        streak.setPreviousLogin(yesterdayDate);
        streak.setStreak(3);

        // Act
        authService.checkAndUpdateStreak(user);

        // Assert
        assertEquals(4, streak.getStreak());
        verify(streaksService).saveStreak(streak);
    }

    @Test
    void checkAndUpdateStreak_withOneDayMissed_shouldResetToOne() {
        // Arrange
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        Date twoDaysAgoDate = Date.from(twoDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
        streak.setPreviousLogin(twoDaysAgoDate);
        streak.setStreak(10);

        // Act
        authService.checkAndUpdateStreak(user);

        // Assert
        assertEquals(1, streak.getStreak());
        verify(streaksService).saveStreak(streak);
    }

    @Test
    void checkAndUpdateStreak_withLongAbsence_shouldResetToOne() {
        // Arrange
        LocalDate lastWeek = LocalDate.now().minusWeeks(1);
        Date lastWeekDate = Date.from(lastWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        streak.setPreviousLogin(lastWeekDate);
        streak.setStreak(15);

        // Act
        authService.checkAndUpdateStreak(user);

        // Assert
        assertEquals(1, streak.getStreak());
        verify(streaksService).saveStreak(streak);
    }


    @Test
    void checkAndUpdateStreak_withFutureDate_shouldNotUpdate() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Date tomorrowDate = Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant());
        streak.setPreviousLogin(tomorrowDate);
        int currentStreak = 7;
        streak.setStreak(currentStreak);
        int expectedStreak = 1;

        // Act
        authService.checkAndUpdateStreak(user);

        // Assert
        assertEquals(expectedStreak, streak.getStreak());
    }

    @Test
    void saveStreak_shouldSaveStreak() {
        // Arrange
        when(streaksService.saveStreak(streak)).thenReturn(streak);

        // Act
        Streak savedStreak = streaksService.saveStreak(streak);

        // Assert
        assertNotNull(savedStreak);
        assertEquals(streak, savedStreak);
        verify(streaksService).saveStreak(streak);
    }

}
