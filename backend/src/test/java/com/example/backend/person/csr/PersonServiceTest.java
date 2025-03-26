package com.example.backend.person.csr;

import com.example.backend.person.Person;
import com.example.backend.person.PersonDTO;
import com.example.backend.profile.ProfileDTO;
import com.example.backend.util.ServiceResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private Person testPerson;
    private Person friendPerson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setUsername("testuser");
        testPerson.setPassword("password");
        testPerson.setFriends(new ArrayList<>());
        testPerson.setFavoriteCourses(new ArrayList<>());

        friendPerson = new Person();
        friendPerson.setId(2L);
        friendPerson.setUsername("frienduser");
        friendPerson.setPassword("password");
    }

    @Test
    void findByUsername_whenUserExists_returnsUser() {
        // Setup
        when(personRepository.findByUsername("testuser")).thenReturn(Optional.of(testPerson));

        // Test
        Optional<Person> result = personService.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findByUsername_whenUserDoesNotExist_returnsEmpty() {
        // Setup
        when(personRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Test
        Optional<Person> result = personService.findByUsername("nonexistent");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void saveNewPerson_whenValid_returnsPerson() {
        // Setup
        PersonDTO personDTO = new PersonDTO("newuser", "password");
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);

        // Test
        Optional<Person> result = personService.saveNewPerson(personDTO);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void updatePerson_whenValid_returnsPerson() {
        // Setup
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setFirstName("Updated");
        profileDTO.setLastName("User");
        
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);

        // Test
        Optional<Person> result = personService.updatePerson(testPerson, profileDTO);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void addFriend_whenNotAlreadyFriends_returnsUpdatedPerson() {
        // Setup
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);

        // Test
        Optional<Person> result = personService.addFriend(testPerson, friendPerson);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void addFriend_whenAlreadyFriends_returnsEmpty() {
        // Setup
        testPerson.getFriends().add(friendPerson);

        // Test
        Optional<Person> result = personService.addFriend(testPerson, friendPerson);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void removeFriend_whenFriends_returnsUpdatedPerson() {
        // Setup
        testPerson.getFriends().add(friendPerson);
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);

        // Test
        Optional<Person> result = personService.removeFriend(testPerson, friendPerson);

        // Assert
        assertTrue(result.isPresent());
    }

    @Test
    void removeFriend_whenNotFriends_returnsEmpty() {
        // Setup
        // friends list is empty by default

        // Test
        Optional<Person> result = personService.removeFriend(testPerson, friendPerson);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getFriends_returnsFriendsList() {
        // Setup
        testPerson.getFriends().add(friendPerson);

        // Test
        List<Person> result = personService.getFriends(testPerson);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(friendPerson, result.getFirst());
    }

    @Test
    void getAll_returnsListOfPersons() {
        // Setup
        List<Person> persons = List.of(testPerson);
        when(personRepository.findAll()).thenReturn(persons);

        // Test
        ServiceResult<List<Person>, Void> result = personService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        assertEquals("testuser", result.getData().get(0).getUsername());
    }
}