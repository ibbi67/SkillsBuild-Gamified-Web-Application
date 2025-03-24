package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.badge.BadgeDTO;
import com.example.backend.badge.error.BadgeCreateError;
import com.example.backend.badge.error.BadgeGetError;
import com.example.backend.badge.error.BadgeAwardError;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonRepository;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BadgeServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private BadgeService badgeService;

    private Badge testBadge;
    private Person testPerson;
    private BadgeDTO testBadgeDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testBadge = new Badge(
            "Test Badge",
            "Test Description",
            "/badges/test.png",
            "TEST",
            1
        );
        testBadge.setId(1);

        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setBadges(new HashSet<>());

        testBadgeDTO = new BadgeDTO(
            "Test Badge",
            "Test Description",
            "/badges/test.png",
            "TEST",
            1
        );
    }

    @Test
    void initializeDefaultBadges_Success() {
        when(badgeRepository.count()).thenReturn(0L);
        when(badgeRepository.save(any(Badge.class))).thenReturn(new Badge());

        badgeService.initializeDefaultBadges();

        verify(badgeRepository, times(3)).save(any(Badge.class));
    }

    @Test
    void initializeDefaultBadges_AlreadyInitialized() {
        when(badgeRepository.count()).thenReturn(3L);

        badgeService.initializeDefaultBadges();

        verify(badgeRepository, never()).save(any(Badge.class));
    }

    @Test
    void createBadge_Success() {
        when(badgeRepository.save(any(Badge.class))).thenReturn(testBadge);

        ServiceResult<Badge, BadgeCreateError> result = badgeService.createBadge(testBadgeDTO);

        assertTrue(result.isSuccess());
        assertEquals(testBadge, result.getData());
        verify(badgeRepository).save(any(Badge.class));
    }

    @Test
    void createBadge_Failure() {
        when(badgeRepository.save(any(Badge.class))).thenThrow(new RuntimeException());

        ServiceResult<Badge, BadgeCreateError> result = badgeService.createBadge(testBadgeDTO);

        assertFalse(result.isSuccess());
        assertEquals(BadgeCreateError.BADGE_CREATION_FAILED, result.getError());
        verify(badgeRepository).save(any(Badge.class));
    }

    @Test
    void getAllBadges_Success() {
        List<Badge> badges = Arrays.asList(testBadge);
        when(badgeRepository.findAll()).thenReturn(badges);

        ServiceResult<List<Badge>, BadgeGetError> result = badgeService.getAllBadges();

        assertTrue(result.isSuccess());
        assertEquals(badges, result.getData());
        verify(badgeRepository).findAll();
    }

    @Test
    void getAllBadges_EmptyList() {
        when(badgeRepository.findAll()).thenReturn(List.of());

        ServiceResult<List<Badge>, BadgeGetError> result = badgeService.getAllBadges();

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetError.BADGE_NOT_FOUND, result.getError());
        verify(badgeRepository).findAll();
    }

    @Test
    void getAllBadges_Failure() {
        when(badgeRepository.findAll()).thenThrow(new RuntimeException());

        ServiceResult<List<Badge>, BadgeGetError> result = badgeService.getAllBadges();

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, result.getError());
        verify(badgeRepository).findAll();
    }

    @Test
    void getBadgeById_Success() {
        when(badgeRepository.findById(1)).thenReturn(Optional.of(testBadge));

        ServiceResult<Badge, BadgeGetError> result = badgeService.getBadgeById(1);

        assertTrue(result.isSuccess());
        assertEquals(testBadge, result.getData());
        verify(badgeRepository).findById(1);
    }

    @Test
    void getBadgeById_NotFound() {
        when(badgeRepository.findById(1)).thenReturn(Optional.empty());

        ServiceResult<Badge, BadgeGetError> result = badgeService.getBadgeById(1);

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetError.BADGE_NOT_FOUND, result.getError());
        verify(badgeRepository).findById(1);
    }

    @Test
    void getBadgeById_InvalidId() {
        ServiceResult<Badge, BadgeGetError> result = badgeService.getBadgeById(null);

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, result.getError());
        verify(badgeRepository, never()).findById(any());
    }

    @Test
    void getBadgeByName_Success() {
        when(badgeRepository.findByName("Test Badge")).thenReturn(Optional.of(testBadge));

        ServiceResult<Badge, BadgeGetError> result = badgeService.getBadgeByName("Test Badge");

        assertTrue(result.isSuccess());
        assertEquals(testBadge, result.getData());
        verify(badgeRepository).findByName("Test Badge");
    }

    @Test
    void getBadgeByName_NotFound() {
        when(badgeRepository.findByName("Non-existent Badge")).thenReturn(Optional.empty());

        ServiceResult<Badge, BadgeGetError> result = badgeService.getBadgeByName("Non-existent Badge");

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetError.BADGE_NOT_FOUND, result.getError());
        verify(badgeRepository).findByName("Non-existent Badge");
    }

    @Test
    void awardBadgeToUser_Success() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        when(badgeRepository.findById(1)).thenReturn(Optional.of(testBadge));
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);
        when(badgeRepository.save(any(Badge.class))).thenReturn(testBadge);

        ServiceResult<Void, BadgeAwardError> result = badgeService.awardBadgeToUser(1, 1);

        assertTrue(result.isSuccess());
        assertTrue(testPerson.getBadges().contains(testBadge));
        verify(personRepository).save(testPerson);
        verify(badgeRepository).save(testBadge);
    }

    @Test
    void awardBadgeToUser_UserNotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceResult<Void, BadgeAwardError> result = badgeService.awardBadgeToUser(1, 1);

        assertFalse(result.isSuccess());
        assertEquals(BadgeAwardError.USER_NOT_FOUND, result.getError());
        verify(personRepository, never()).save(any());
        verify(badgeRepository, never()).save(any());
    }

    @Test
    void awardBadgeToUser_BadgeNotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        when(badgeRepository.findById(1)).thenReturn(Optional.empty());

        ServiceResult<Void, BadgeAwardError> result = badgeService.awardBadgeToUser(1, 1);

        assertFalse(result.isSuccess());
        assertEquals(BadgeAwardError.BADGE_NOT_FOUND, result.getError());
        verify(personRepository, never()).save(any());
        verify(badgeRepository, never()).save(any());
    }

    @Test
    void getUserBadges_Success() {
        Set<Badge> badges = new HashSet<>(Arrays.asList(testBadge));
        testPerson.setBadges(badges);
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));

        ServiceResult<Set<Badge>, BadgeGetError> result = badgeService.getUserBadges(1);

        assertTrue(result.isSuccess());
        assertEquals(badges, result.getData());
        verify(personRepository).findById(1L);
    }

    @Test
    void getUserBadges_UserNotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        ServiceResult<Set<Badge>, BadgeGetError> result = badgeService.getUserBadges(1);

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, result.getError());
        verify(personRepository).findById(1L);
    }

    @Test
    void getUserBadges_InvalidId() {
        ServiceResult<Set<Badge>, BadgeGetError> result = badgeService.getUserBadges(null);

        assertFalse(result.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, result.getError());
        verify(personRepository, never()).findById(any());
    }
} 