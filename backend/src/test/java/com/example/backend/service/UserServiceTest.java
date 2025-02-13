package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.backend.domain.User;
import com.example.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Arrange
        User mockUser = new User();
        mockUser.setId(100);
        mockUser.setUsername("testuser");
        mockUser.setPassword("encodedPassword");
        when(userRepository.findByUsername("testuser")).thenReturn(mockUser);

        // Act
        User foundUser = userService.findByUsername("testuser");

        // Assert
        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void findByUsername_WhenUserNotExist_ShouldReturnNull() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        // Act
        User foundUser = userService.findByUsername("unknown");

        // Assert
        assertNull(foundUser);
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void save_WhenNewUser_ShouldEncodePasswordAndReturnSavedUser() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("plaintextPassword");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername("newuser");
        savedUser.setPassword("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.save(newUser);

        // Assert
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals(1, result.getId());

        verify(userRepository).save(
            argThat(user -> {
                return passwordEncoder.matches("plaintextPassword", user.getPassword());
            })
        );
    }

    @Test
    void save_WhenDuplicateUser_ShouldReturnNull() {
        // Arrange
        User duplicatedUser = new User();
        duplicatedUser.setUsername("dupeUsername");
        duplicatedUser.setPassword("plaintextPassword");

        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        // Act
        User result = userService.save(duplicatedUser);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
