package com.example.backend.friend.csr;

import com.example.backend.friend.FriendDTO;
import com.example.backend.friend.FriendResponseDTO;
import com.example.backend.friend.error.FriendAddError;
import com.example.backend.friend.error.FriendGetAllError;
import com.example.backend.friend.error.FriendRemoveError;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.util.JWT;
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

class FriendServiceTest {

    @Mock
    private JWT jwt;

    @Mock
    private PersonService personService;

    @InjectMocks
    private FriendService friendService;

    private Person testPerson;
    private Person friendPerson;
    private FriendDTO friendDTO;
    private final String validToken = "valid-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testPerson = new Person();
        testPerson.setId(1L);
        testPerson.setUsername("test-username");
        testPerson.setFirstName("Test");
        testPerson.setLastName("User");
        testPerson.setEmail("test@example.com");
        testPerson.setAvatarLink("avatar.jpg");
        testPerson.setStreak(5);
        testPerson.setFriends(new ArrayList<>());

        friendPerson = new Person();
        friendPerson.setId(2L);
        friendPerson.setUsername("friend-username");
        friendPerson.setFirstName("Friend");
        friendPerson.setLastName("User");
        friendPerson.setEmail("friend@example.com");
        friendPerson.setAvatarLink("friend-avatar.jpg");
        friendPerson.setStreak(3);
        
        friendDTO = new FriendDTO(
            friendPerson.getId()
        );
        
        when(jwt.getPersonFromToken(validToken)).thenReturn(Optional.of(testPerson));
        when(jwt.getPersonFromToken("invalidToken")).thenReturn(Optional.empty());
    }

    @Test
    void getAllFriends_withValidToken_returnsEmptyList() {
        // Test
        ServiceResult<List<FriendResponseDTO>, FriendGetAllError> result = friendService.getAllFriends(validToken);
        
        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void getAllFriends_withInvalidToken_returnsError() {
        // Test
        ServiceResult<List<FriendResponseDTO>, FriendGetAllError> result = friendService.getAllFriends("invalidToken");
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals(FriendGetAllError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    void getAllFriends_withFriends_returnsFriendsList() {
        // Setup
        testPerson.getFriends().add(friendPerson);
        
        // Test
        ServiceResult<List<FriendResponseDTO>, FriendGetAllError> result = friendService.getAllFriends(validToken);
        
        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        FriendResponseDTO friendResponseDTO = result.getData().getFirst();
        assertEquals(friendPerson.getUsername(), friendResponseDTO.getUsername());
        assertEquals(friendPerson.getFirstName(), friendResponseDTO.getFirstName());
        assertEquals(friendPerson.getLastName(), friendResponseDTO.getLastName());
        assertEquals(friendPerson.getEmail(), friendResponseDTO.getEmail());
        assertEquals(friendPerson.getAvatarLink(), friendResponseDTO.getAvatarLink());
    }

    @Test
    void addFriend_withValidTokenAndDTO_success() {
        // Setup
        when(personService.findById(friendDTO.getPersonId())).thenReturn(Optional.of(friendPerson));
        when(personService.save(any(Person.class))).thenReturn(Optional.of(testPerson));
        
        // Test
        ServiceResult<Void, FriendAddError> result = friendService.addFriend(validToken, friendDTO);
        
        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void addFriend_withInvalidToken_returnsError() {
        // Test
        ServiceResult<Void, FriendAddError> result = friendService.addFriend("invalidToken", friendDTO);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals(FriendAddError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    void addFriend_withNonExistentPerson_returnsError() {
        // Setup
        when(personService.findById(friendDTO.getPersonId())).thenReturn(Optional.empty());
        
        // Test
        ServiceResult<Void, FriendAddError> result = friendService.addFriend(validToken, friendDTO);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals(FriendAddError.PERSON_NOT_FOUND, result.getError());
    }

    @Test
    void addFriend_whenAlreadyFriends_returnsError() {
        // Setup
        testPerson.getFriends().add(friendPerson);
        when(personService.findById(friendDTO.getPersonId())).thenReturn(Optional.of(friendPerson));
        
        // Test
        ServiceResult<Void, FriendAddError> result = friendService.addFriend(validToken, friendDTO);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals(FriendAddError.ALREADY_FRIENDS, result.getError());
    }

    @Test
    void addFriend_withSameId_returnsError() {
        // Setup
        FriendDTO selfDTO = new FriendDTO(
            testPerson.getId()
        );
        // The FriendDTO constructor doesn't set the personId, so we need to set it separately
        when(personService.findById(testPerson.getId())).thenReturn(Optional.of(testPerson));
        
        // Test
        ServiceResult<Void, FriendAddError> result = friendService.addFriend(validToken, selfDTO);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals(FriendAddError.CANNOT_ADD_SELF, result.getError());
    }

    @Test
    void removeFriend_withValidTokenAndDTO_success() {
        // Setup
        testPerson.getFriends().add(friendPerson);
        when(personService.findById(friendDTO.getPersonId())).thenReturn(Optional.of(friendPerson));
        when(personService.save(any(Person.class))).thenReturn(Optional.of(testPerson));
        
        // Test
        ServiceResult<Void, FriendRemoveError> result = friendService.removeFriend(validToken, friendDTO);
        
        // Assert
        assertTrue(result.isSuccess());
    }

    @Test
    void removeFriend_withInvalidToken_returnsError() {
        // Test
        ServiceResult<Void, FriendRemoveError> result = friendService.removeFriend("invalidToken", friendDTO);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals(FriendRemoveError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    void removeFriend_withNonExistentPerson_returnsError() {
        // Setup
        when(personService.findById(friendDTO.getPersonId())).thenReturn(Optional.empty());
        
        // Test
        ServiceResult<Void, FriendRemoveError> result = friendService.removeFriend(validToken, friendDTO);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals(FriendRemoveError.PERSON_NOT_FOUND, result.getError());
    }

    @Test
    void removeFriend_whenNotFriends_returnsError() {
        // Setup
        when(personService.findById(friendDTO.getPersonId())).thenReturn(Optional.of(friendPerson));
        
        // Test
        ServiceResult<Void, FriendRemoveError> result = friendService.removeFriend(validToken, friendDTO);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals(FriendRemoveError.NOT_FRIENDS, result.getError());
    }
}