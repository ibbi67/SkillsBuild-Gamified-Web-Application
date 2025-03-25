package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.badge.BadgeDTO;
import com.example.backend.badge.error.BadgeAwardError;
import com.example.backend.badge.error.BadgeCreateError;
import com.example.backend.badge.error.BadgeGetError;
import com.example.backend.course.Course;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonRepository;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

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
        testPerson.setUsername("testuser");
        testPerson.setBadges(new HashSet<>());
        testPerson.setFavoriteCourses(new ArrayList<>());

        testBadgeDTO = new BadgeDTO(
            "Test Badge",
            "Test Description",
            "/badges/test.png",
            "TEST",
            1
        );
    }

    @Test
    void testEntityAndDTOProperties() {
        // Test Badge entity
        Badge badge = new Badge();
        badge.setId(1);
        assertEquals(1, badge.getId());
        
        badge.setName("Updated Name");
        assertEquals("Updated Name", badge.getName());
        
        badge.setDescription("Updated Description");
        assertEquals("Updated Description", badge.getDescription());
        
        badge.setImageUrl("Updated URL");
        assertEquals("Updated URL", badge.getImageUrl());
        
        badge.setCriteriaType("Updated Type");
        assertEquals("Updated Type", badge.getCriteriaType());
        
        badge.setCriteriaValue(10);
        assertEquals(10, badge.getCriteriaValue());
        
        Set<Person> persons = new HashSet<>();
        badge.setPersons(persons);
        assertEquals(persons, badge.getPersons());
        
        Person person = new Person();
        badge.addPerson(person);
        assertTrue(badge.getPersons().contains(person));
        
        badge.removePerson(person);
        assertFalse(badge.getPersons().contains(person));
        
        // Test BadgeDTO
        BadgeDTO dto = new BadgeDTO("Name", "Desc", "URL", "Type", 1);
        assertEquals("Name", dto.getName());
        assertEquals("Desc", dto.getDescription());
        assertEquals("URL", dto.getImageUrl());
        assertEquals("Type", dto.getCriteriaType());
        assertEquals(1, dto.getCriteriaValue());
        
        dto.setName("Updated DTO Name");
        assertEquals("Updated DTO Name", dto.getName());
    }

    @Test
    void testServiceBasicOperations() {
        // Test initialize default badges - when no badges exist
        when(badgeRepository.count()).thenReturn(0L);
        badgeService.initializeDefaultBadges();
        verify(badgeRepository, times(5)).save(any(Badge.class));
        
        // Test initialize default badges - when badges already exist
        reset(badgeRepository);
        when(badgeRepository.count()).thenReturn(5L);
        badgeService.initializeDefaultBadges();
        verify(badgeRepository, never()).save(any(Badge.class));
        
        // Test create badge - success
        reset(badgeRepository);
        when(badgeRepository.save(any(Badge.class))).thenReturn(testBadge);
        ServiceResult<Badge, BadgeCreateError> successResult = badgeService.createBadge(testBadgeDTO);
        assertTrue(successResult.isSuccess());
        assertEquals(testBadge, successResult.getData());
        
        // Test create badge - failure
        when(badgeRepository.save(any(Badge.class))).thenThrow(new RuntimeException("DB Error"));
        ServiceResult<Badge, BadgeCreateError> failureResult = badgeService.createBadge(testBadgeDTO);
        assertFalse(failureResult.isSuccess());
        assertEquals(BadgeCreateError.BADGE_CREATION_FAILED, failureResult.getError());
        
        // Test create badge - null DTO
        ServiceResult<Badge, BadgeCreateError> nullDtoResult = badgeService.createBadge(null);
        assertFalse(nullDtoResult.isSuccess());
        assertEquals(BadgeCreateError.BADGE_CREATION_FAILED, nullDtoResult.getError());
    }

    @Test
    void testGetOperations() {
        // Test getAllBadges - success
        List<Badge> badges = List.of(testBadge);
        when(badgeRepository.findAll()).thenReturn(badges);
        ServiceResult<List<Badge>, BadgeGetError> getAllSuccess = badgeService.getAllBadges();
        assertTrue(getAllSuccess.isSuccess());
        assertEquals(badges, getAllSuccess.getData());
        
        // Test getAllBadges - empty list
        when(badgeRepository.findAll()).thenReturn(Collections.emptyList());
        ServiceResult<List<Badge>, BadgeGetError> getAllEmpty = badgeService.getAllBadges();
        assertFalse(getAllEmpty.isSuccess());
        assertEquals(BadgeGetError.BADGE_NOT_FOUND, getAllEmpty.getError());
        
        // Test getAllBadges - exception
        when(badgeRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        ServiceResult<List<Badge>, BadgeGetError> getAllError = badgeService.getAllBadges();
        assertFalse(getAllError.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, getAllError.getError());
        
        // Test getBadgeById - success
        when(badgeRepository.findById(1)).thenReturn(Optional.of(testBadge));
        ServiceResult<Badge, BadgeGetError> getByIdSuccess = badgeService.getBadgeById(1);
        assertTrue(getByIdSuccess.isSuccess());
        assertEquals(testBadge, getByIdSuccess.getData());
        
        // Test getBadgeById - not found
        when(badgeRepository.findById(99)).thenReturn(Optional.empty());
        ServiceResult<Badge, BadgeGetError> getByIdNotFound = badgeService.getBadgeById(99);
        assertFalse(getByIdNotFound.isSuccess());
        assertEquals(BadgeGetError.BADGE_NOT_FOUND, getByIdNotFound.getError());
        
        // Test getBadgeById - exception
        when(badgeRepository.findById(55)).thenThrow(new RuntimeException("DB error"));
        ServiceResult<Badge, BadgeGetError> getByIdError = badgeService.getBadgeById(55);
        assertFalse(getByIdError.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, getByIdError.getError());
        
        // Reset mocks for the next tests to avoid interference
        reset(badgeRepository);
        
        // Test getBadgeByName - success
        when(badgeRepository.findByName("Test Badge")).thenReturn(Optional.of(testBadge));
        ServiceResult<Badge, BadgeGetError> getByNameSuccess = badgeService.getBadgeByName("Test Badge");
        assertTrue(getByNameSuccess.isSuccess());
        assertEquals(testBadge, getByNameSuccess.getData());
        
        // Test getBadgeByName - not found
        when(badgeRepository.findByName("NonExistent")).thenReturn(Optional.empty());
        ServiceResult<Badge, BadgeGetError> getByNameNotFound = badgeService.getBadgeByName("NonExistent");
        assertFalse(getByNameNotFound.isSuccess());
        assertEquals(BadgeGetError.BADGE_NOT_FOUND, getByNameNotFound.getError());
        
        // Test getBadgeByName - exception
        when(badgeRepository.findByName("Error")).thenThrow(new RuntimeException("DB error"));
        ServiceResult<Badge, BadgeGetError> getByNameError = badgeService.getBadgeByName("Error");
        assertFalse(getByNameError.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, getByNameError.getError());
        
        // Reset mocks for the next tests
        reset(badgeRepository);
        
        // Test getBadgesByCriteriaType - success
        when(badgeRepository.findByCriteriaType("TEST")).thenReturn(badges);
        ServiceResult<List<Badge>, BadgeGetError> getByCriteriaSuccess = badgeService.getBadgesByCriteriaType("TEST");
        assertTrue(getByCriteriaSuccess.isSuccess());
        assertEquals(badges, getByCriteriaSuccess.getData());
        
        // Test getBadgesByCriteriaType - not found
        when(badgeRepository.findByCriteriaType("NONEXISTENT")).thenReturn(Collections.emptyList());
        ServiceResult<List<Badge>, BadgeGetError> getByCriteriaNotFound = badgeService.getBadgesByCriteriaType("NONEXISTENT");
        assertFalse(getByCriteriaNotFound.isSuccess());
        assertEquals(BadgeGetError.BADGE_NOT_FOUND, getByCriteriaNotFound.getError());
        
        // Test getBadgesByCriteriaType - exception
        when(badgeRepository.findByCriteriaType("ERROR")).thenThrow(new RuntimeException("DB error"));
        ServiceResult<List<Badge>, BadgeGetError> getByCriteriaError = badgeService.getBadgesByCriteriaType("ERROR");
        assertFalse(getByCriteriaError.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, getByCriteriaError.getError());
        
        // Reset mocks for the next tests
        reset(badgeRepository, personRepository);
        
        // Test null/empty parameters
        assertFalse(badgeService.getBadgeById(null).isSuccess());
        assertFalse(badgeService.getBadgeByName(null).isSuccess());
        assertFalse(badgeService.getBadgeByName("").isSuccess());
        assertFalse(badgeService.getBadgesByCriteriaType(null).isSuccess());
        assertFalse(badgeService.getBadgesByCriteriaType("").isSuccess());
        
        // Test getUserBadges - success
        Set<Badge> badgeSet = new HashSet<>(badges);
        testPerson.setBadges(badgeSet);
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        ServiceResult<Set<Badge>, BadgeGetError> getUserBadgesSuccess = badgeService.getUserBadges(1);
        assertTrue(getUserBadgesSuccess.isSuccess());
        assertEquals(badgeSet, getUserBadgesSuccess.getData());
        
        // Test getUserBadges - user not found
        when(personRepository.findById(99L)).thenReturn(Optional.empty());
        ServiceResult<Set<Badge>, BadgeGetError> getUserBadgesNotFound = badgeService.getUserBadges(99);
        assertFalse(getUserBadgesNotFound.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, getUserBadgesNotFound.getError());
        
        // Test getUserBadges - exception
        when(personRepository.findById(55L)).thenThrow(new RuntimeException("DB error"));
        ServiceResult<Set<Badge>, BadgeGetError> getUserBadgesError = badgeService.getUserBadges(55);
        assertFalse(getUserBadgesError.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, getUserBadgesError.getError());
        
        // Test getUserBadges - null ID
        ServiceResult<Set<Badge>, BadgeGetError> getUserBadgesNullId = badgeService.getUserBadges(null);
        assertFalse(getUserBadgesNullId.isSuccess());
        assertEquals(BadgeGetError.GET_BADGE_FAILED, getUserBadgesNullId.getError());
    }

    @Test
    void testAwardBadgeToUser() {
        // Setup mocks
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        when(badgeRepository.findById(1)).thenReturn(Optional.of(testBadge));
        
        // Test success case
        ServiceResult<Void, BadgeAwardError> awardSuccess = badgeService.awardBadgeToUser(1, 1);
        assertTrue(awardSuccess.isSuccess());
        verify(personRepository).save(testPerson);
        
        // Test user not found
        reset(personRepository, badgeRepository);
        when(personRepository.findById(99L)).thenReturn(Optional.empty());
        ServiceResult<Void, BadgeAwardError> awardUserNotFound = badgeService.awardBadgeToUser(99, 1);
        assertFalse(awardUserNotFound.isSuccess());
        assertEquals(BadgeAwardError.USER_NOT_FOUND, awardUserNotFound.getError());
        
        // Test badge not found
        reset(personRepository, badgeRepository);
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        when(badgeRepository.findById(99)).thenReturn(Optional.empty());
        ServiceResult<Void, BadgeAwardError> awardBadgeNotFound = badgeService.awardBadgeToUser(1, 99);
        assertFalse(awardBadgeNotFound.isSuccess());
        assertEquals(BadgeAwardError.BADGE_NOT_FOUND, awardBadgeNotFound.getError());
        
        // Test repository exception
        reset(personRepository, badgeRepository);
        when(personRepository.findById(1L)).thenReturn(Optional.of(testPerson));
        when(badgeRepository.findById(1)).thenReturn(Optional.of(testBadge));
        when(personRepository.save(any(Person.class))).thenThrow(new RuntimeException("DB error"));
        ServiceResult<Void, BadgeAwardError> awardSaveError = badgeService.awardBadgeToUser(1, 1);
        assertFalse(awardSaveError.isSuccess());
        assertEquals(BadgeAwardError.BADGE_AWARD_FAILED, awardSaveError.getError());
        
        // Test null IDs
        reset(personRepository, badgeRepository);
        ServiceResult<Void, BadgeAwardError> nullUserResult = badgeService.awardBadgeToUser(null, 1);
        assertFalse(nullUserResult.isSuccess());
        assertEquals(BadgeAwardError.BADGE_AWARD_FAILED, nullUserResult.getError());
        
        ServiceResult<Void, BadgeAwardError> nullBadgeResult = badgeService.awardBadgeToUser(1, null);
        assertFalse(nullBadgeResult.isSuccess());
        assertEquals(BadgeAwardError.BADGE_AWARD_FAILED, nullBadgeResult.getError());
        
        // Test already awarded badge (idempotent operation)
        reset(personRepository, badgeRepository);
        Person personWithBadge = new Person();
        personWithBadge.setId(1L);
        personWithBadge.setBadges(new HashSet<>());
        personWithBadge.getBadges().add(testBadge);
        
        when(personRepository.findById(1L)).thenReturn(Optional.of(personWithBadge));
        when(badgeRepository.findById(1)).thenReturn(Optional.of(testBadge));
        when(personRepository.save(any(Person.class))).thenReturn(personWithBadge);
        
        ServiceResult<Void, BadgeAwardError> alreadyAwardedResult = badgeService.awardBadgeToUser(1, 1);
        assertTrue(alreadyAwardedResult.isSuccess());
        verify(personRepository).save(personWithBadge);
    }
    
    @Test
    void testCheckAndAwardFavoriteBadges() {
        // Setup test data
        Person person = new Person();
        person.setId(1L);
        person.setBadges(new HashSet<>());
        
        // Add favorite courses
        List<Course> courses = new ArrayList<>();
        courses.add(new Course());
        courses.add(new Course());
        courses.add(new Course());
        person.setFavoriteCourses(courses);
        
        // Setup badges
        Badge favBadge1 = new Badge("Favorite 1", "Desc", "url", "FAVORITE", 1);
        Badge favBadge5 = new Badge("Favorite 5", "Desc", "url", "FAVORITE", 5);
        List<Badge> favoriteBadges = List.of(favBadge1, favBadge5);
        
        // Setup mocks
        when(badgeRepository.findByCriteriaType("FAVORITE")).thenReturn(favoriteBadges);
        when(personRepository.save(any(Person.class))).thenReturn(person);
        
        // Test favorite badges criteria
        badgeService.checkAndAwardFavoriteBadges(person);
        verify(personRepository).save(person);
        assertTrue(person.getBadges().contains(favBadge1));
        assertFalse(person.getBadges().contains(favBadge5));
        
        // Test with null person
        reset(badgeRepository, personRepository);
        // No exception should be thrown
        badgeService.checkAndAwardFavoriteBadges(null);
        verify(personRepository, never()).save(any(Person.class));
        
        // Test with null favorites list
        reset(badgeRepository, personRepository);
        Person personNullFavorites = new Person();
        personNullFavorites.setId(1L);
        personNullFavorites.setBadges(new HashSet<>());
        personNullFavorites.setFavoriteCourses(null);
        
        when(badgeRepository.findByCriteriaType("FAVORITE")).thenReturn(favoriteBadges);
        badgeService.checkAndAwardFavoriteBadges(personNullFavorites);
        verify(personRepository, never()).save(any(Person.class));
        
        // Test with empty favorites list
        reset(badgeRepository, personRepository);
        Person personEmptyFavorites = new Person();
        personEmptyFavorites.setId(1L);
        personEmptyFavorites.setBadges(new HashSet<>());
        personEmptyFavorites.setFavoriteCourses(new ArrayList<>());
        
        when(badgeRepository.findByCriteriaType("FAVORITE")).thenReturn(favoriteBadges);
        badgeService.checkAndAwardFavoriteBadges(personEmptyFavorites);
        verify(personRepository, never()).save(any(Person.class));
        
        // Test with repository exception - should be caught internally and not propagate
        reset(badgeRepository, personRepository);
        Person personForErrorTest = new Person();
        personForErrorTest.setId(1L);
        personForErrorTest.setBadges(new HashSet<>());
        List<Course> coursesForError = new ArrayList<>();
        coursesForError.add(new Course());
        personForErrorTest.setFavoriteCourses(coursesForError);
        
        when(badgeRepository.findByCriteriaType("FAVORITE")).thenThrow(new RuntimeException("DB error"));
        
        // This should not throw an exception
        try {
            badgeService.checkAndAwardFavoriteBadges(personForErrorTest);
            // Test passes if no exception is thrown
        } catch (Exception e) {
            fail("Exception should not be propagated: " + e.getMessage());
        }
    }
    
    @Test
    void testCheckAndAwardStreakBadges() {
        // Setup test data
        Person person = new Person();
        person.setId(1L);
        person.setBadges(new HashSet<>());
        person.setStreak(5); // Has 5-day streak
        
        // Setup badges
        Badge streakBadge3 = new Badge("Streak 3", "Desc", "url", "STREAK", 3);
        Badge streakBadge7 = new Badge("Streak 7", "Desc", "url", "STREAK", 7);
        List<Badge> streakBadges = List.of(streakBadge3, streakBadge7);
        
        // Setup mocks
        when(badgeRepository.findByCriteriaType("STREAK")).thenReturn(streakBadges);
        when(personRepository.save(any(Person.class))).thenReturn(person);
        
        // Test streak badges
        badgeService.checkAndAwardStreakBadges(person);
        verify(personRepository).save(person);
        assertTrue(person.getBadges().contains(streakBadge3));
        assertFalse(person.getBadges().contains(streakBadge7));
        
        // Test with null person
        reset(badgeRepository, personRepository);
        // No exception should be thrown
        badgeService.checkAndAwardStreakBadges(null);
        verify(personRepository, never()).save(any(Person.class));
        
        // Test with zero streak
        reset(badgeRepository, personRepository);
        Person personZeroStreak = new Person();
        personZeroStreak.setId(1L);
        personZeroStreak.setBadges(new HashSet<>());
        personZeroStreak.setStreak(0);
        
        when(badgeRepository.findByCriteriaType("STREAK")).thenReturn(streakBadges);
        badgeService.checkAndAwardStreakBadges(personZeroStreak);
        verify(personRepository, never()).save(any(Person.class));
        
        // Test with repository exception - should be caught internally and not propagate
        reset(badgeRepository, personRepository);
        Person personForErrorTest = new Person();
        personForErrorTest.setId(1L);
        personForErrorTest.setBadges(new HashSet<>());
        personForErrorTest.setStreak(3);
        
        when(badgeRepository.findByCriteriaType("STREAK")).thenThrow(new RuntimeException("DB error"));
        
        // This should not throw an exception
        try {
            badgeService.checkAndAwardStreakBadges(personForErrorTest);
            // Test passes if no exception is thrown
        } catch (Exception e) {
            fail("Exception should not be propagated: " + e.getMessage());
        }
    }
    
    @Test
    void testFindBadgeByCriteriaTypeAndValue() {
        // Setup mocks
        when(badgeRepository.findByCriteriaTypeAndCriteriaValue("TEST", 1))
            .thenReturn(Optional.of(testBadge));
        
        // Test found
        Optional<Badge> foundBadge = badgeService.findBadgeByCriteriaTypeAndValue("TEST", 1);
        assertTrue(foundBadge.isPresent());
        assertEquals(testBadge, foundBadge.get());
        
        // Test not found
        when(badgeRepository.findByCriteriaTypeAndCriteriaValue("NONEXISTENT", 99))
            .thenReturn(Optional.empty());
        Optional<Badge> notFoundBadge = badgeService.findBadgeByCriteriaTypeAndValue("NONEXISTENT", 99);
        assertFalse(notFoundBadge.isPresent());
        
        // Test repository exception - this method should handle exceptions internally
        when(badgeRepository.findByCriteriaTypeAndCriteriaValue("ERROR", 1))
            .thenThrow(new RuntimeException("DB error"));
        
        // This should return an empty Optional instead of throwing an exception
        Optional<Badge> errorBadge = badgeService.findBadgeByCriteriaTypeAndValue("ERROR", 1);
        assertFalse(errorBadge.isPresent());
        
        // Test with null values
        Optional<Badge> nullTypeBadge = badgeService.findBadgeByCriteriaTypeAndValue(null, 1);
        assertFalse(nullTypeBadge.isPresent());
        
        Optional<Badge> nullValueBadge = badgeService.findBadgeByCriteriaTypeAndValue("TEST", null);
        assertFalse(nullValueBadge.isPresent());
    }
    
    @Test
    void testErrorMessages() {
        // Verify all error message enums return expected messages
        assertEquals("Badge not found", BadgeGetError.BADGE_NOT_FOUND.getMessage());
        assertEquals("Failed to retrieve badge", BadgeGetError.GET_BADGE_FAILED.getMessage());
        
        assertEquals("Failed to create badge", BadgeCreateError.BADGE_CREATION_FAILED.getMessage());
        assertEquals("Invalid access token", BadgeCreateError.INVALID_ACCESS_TOKEN.getMessage());
        assertEquals("Only administrators can create badges", BadgeCreateError.UNAUTHORIZED.getMessage());
        
        assertEquals("Failed to award badge", BadgeAwardError.BADGE_AWARD_FAILED.getMessage());
        assertEquals("Invalid access token", BadgeAwardError.INVALID_ACCESS_TOKEN.getMessage());
        assertEquals("Only administrators can award badges", BadgeAwardError.UNAUTHORIZED.getMessage());
        assertEquals("User not found", BadgeAwardError.USER_NOT_FOUND.getMessage());
        assertEquals("Badge not found", BadgeAwardError.BADGE_NOT_FOUND.getMessage());
    }
} 