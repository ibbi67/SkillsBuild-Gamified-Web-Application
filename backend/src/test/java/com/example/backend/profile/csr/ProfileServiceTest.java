package com.example.backend.profile.csr;

import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.profile.ProfileDTO;
import com.example.backend.profile.error.ProfileUpdateError;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {
    @Mock
    private JWT jwt;

    @Mock
    private PersonService personService;

    @InjectMocks
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdate_InvalidAccessToken() {
        when(jwt.getPersonFromToken(anyString())).thenReturn(Optional.empty());
        ProfileDTO profileDTO = new ProfileDTO("username", "password", "firstName", "lastName", "email", "avatarLink");
        ServiceResult<Void, ProfileUpdateError> result = profileService.update("invalidToken", profileDTO);
        assertFalse(result.isSuccess());
        assertEquals(ProfileUpdateError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    void testUpdate_Success() {
        Person person = new Person("username", "password", "firstName", "lastName", "email", "avatarLink");
        when(jwt.getPersonFromToken(anyString())).thenReturn(Optional.of(person));
        ProfileDTO profileDTO = new ProfileDTO("username", "password", "newFirstName", "newLastName", "newEmail", "newAvatarLink");
        when(personService.updatePerson(any(Person.class), any(ProfileDTO.class))).thenReturn(Optional.of(person));
        ServiceResult<Void, ProfileUpdateError> result = profileService.update("validToken", profileDTO);
        assertTrue(result.isSuccess());
    }

    @Test
    void testUpdate_ProfileUpdateFailed() {
        Person person = new Person("username", "password", "firstName", "lastName", "email", "avatarLink");
        when(jwt.getPersonFromToken(anyString())).thenReturn(Optional.of(person));
        ProfileDTO profileDTO = new ProfileDTO("username", "password", "newFirstName", "newLastName", "newEmail", "newAvatarLink");
        when(personService.updatePerson(any(Person.class), any(ProfileDTO.class))).thenReturn(Optional.empty());
        ServiceResult<Void, ProfileUpdateError> result = profileService.update("validToken", profileDTO);
        assertFalse(result.isSuccess());
        assertEquals(ProfileUpdateError.PROFILE_UPDATE_FAILED, result.getError());
    }
}
