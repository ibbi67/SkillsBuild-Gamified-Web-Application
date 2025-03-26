package com.example.backend.person;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void testPersonConstructorWithAllFields() {
        String username = "testuser";
        String password = "password";
        Integer streak = 5;
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String avatarLink = "http://example.com/avatar.jpg";

        Person person = new Person(username, password, streak, firstName, lastName, email, avatarLink);

        assertEquals(username, person.getUsername());
        assertEquals(password, person.getPassword());
        assertEquals(streak, person.getStreak());
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
        assertEquals(email, person.getEmail());
        assertEquals(avatarLink, person.getAvatarLink());
    }
}