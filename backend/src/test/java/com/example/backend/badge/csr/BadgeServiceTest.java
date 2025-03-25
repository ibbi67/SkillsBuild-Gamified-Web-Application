package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.badge.error.BadgeGetByIdError;
import com.example.backend.badge.error.BadgeGetByUserError;
import com.example.backend.course.Course;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BadgeServiceTest {

    @Mock
    private JWT jwt;

    @Mock
    private PersonService personService;

    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private BadgeService badgeService;

    private Person testPerson;
    private Badge streakBadge;
    private Badge favoriteBadge;
    private final String validToken = "validToken";

    @BeforeEach
    void setUp() {
        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setUsername("testuser");
        testPerson.setBadges(new ArrayList<>());
        testPerson.setFavoriteCourses(new ArrayList<>());
        testPerson.setStreak(3);

        streakBadge = new Badge(1, "Streak Badge", "3-day streak", "badge.png", "STREAK", 3);
        favoriteBadge = new Badge(2, "Favorite Badge", "1 favorite", "badge.png", "FAVORITE", 1);

        when(jwt.getPersonFromToken(validToken)).thenReturn(Optional.of(testPerson));
        when(jwt.getPersonFromToken("invalidToken")).thenReturn(Optional.empty());
    }

    @Test
    void testGetAllBadges() {
        List<Badge> expectedBadges = Arrays.asList(streakBadge, favoriteBadge);
        when(badgeRepository.findAll()).thenReturn(expectedBadges);

        ServiceResult<List<Badge>, Void> result = badgeService.getAllBadges();

        assertTrue(result.isSuccess());
        assertEquals(expectedBadges, result.getData());
    }

    @Test
    void testGetBadgeById_Success() {
        when(badgeRepository.findById(1)).thenReturn(Optional.of(streakBadge));

        ServiceResult<Badge, BadgeGetByIdError> result = badgeService.getBadgeById(1);

        assertTrue(result.isSuccess());
        assertEquals(streakBadge, result.getData());
    }

    @Test
    void testGetBadgeById_InvalidId() {
        ServiceResult<Badge, BadgeGetByIdError> result = badgeService.getBadgeById(-1);

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetByIdError.INVALID_ID, result.getError());
    }

    @Test
    void testGetBadgeById_NotFound() {
        when(badgeRepository.findById(999)).thenReturn(Optional.empty());

        ServiceResult<Badge, BadgeGetByIdError> result = badgeService.getBadgeById(999);

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetByIdError.BADGE_NOT_FOUND, result.getError());
    }
    
    @Test
    void testGetUserBadges_Success() {
        ServiceResult<List<Badge>, BadgeGetByUserError> result = badgeService.getUserBadges(validToken);

        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(testPerson.getBadges(), result.getData());
    }

    @Test
    void testGetUserBadges_InvalidToken() {
        ServiceResult<List<Badge>, BadgeGetByUserError> result = badgeService.getUserBadges("invalidToken");

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetByUserError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    void testCheckAndAwardStreakBadges() {
        when(badgeRepository.findByCriteriaType("STREAK")).thenReturn(List.of(streakBadge));
        
        badgeService.getUserBadges(validToken); // This will trigger updateBadges internally

        verify(personService, times(1)).save(testPerson);
        assertTrue(testPerson.getBadges().contains(streakBadge));
    }

    @Test
    void testCheckAndAwardFavoriteBadges() {
        testPerson.getFavoriteCourses().add(mock(Course.class));
        when(badgeRepository.findByCriteriaType("FAVORITE")).thenReturn(List.of(favoriteBadge));
        
        badgeService.getUserBadges(validToken); // This will trigger updateBadges internally

        verify(personService, times(1)).save(testPerson);
        assertTrue(testPerson.getBadges().contains(favoriteBadge));
    }

    @Test
    void testCheckAndAwardBadges_NoQualifyingBadges() {
        testPerson.setStreak(1); // Not enough for streak badge
        testPerson.getFavoriteCourses().clear(); // No favorites
        
        when(badgeRepository.findByCriteriaType("STREAK")).thenReturn(List.of(streakBadge));
        when(badgeRepository.findByCriteriaType("FAVORITE")).thenReturn(List.of(favoriteBadge));
        
        badgeService.getUserBadges(validToken);
        
        assertTrue(testPerson.getBadges().isEmpty());
        verify(personService, times(1)).save(testPerson);
    }
}