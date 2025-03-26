package com.example.backend.person.csr;

import com.example.backend.person.Person;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonControllerTest {

    @Mock
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private Person testPerson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testPerson = new Person();
        testPerson.setUsername("testuser");
        testPerson.setPassword("password");
    }

    @Test
    void getAllPersons_returnsListOfPersons() {
        // Setup
        List<Person> persons = List.of(testPerson);
        // Fixing type mismatch by returning a ServiceResult instead of ApiResponse
        when(personService.getAll()).thenReturn(ServiceResult.success(persons));

        // Test
        ResponseEntity<ApiResponse<List<Person>>> response = personController.getAllPersons();

        // Assert
        assertNotNull(response);
        // Replacing deprecated getStatusCodeValue() with getStatusCode().value()
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().size());
        assertEquals("testuser", response.getBody().getData().get(0).getUsername());
    }
}