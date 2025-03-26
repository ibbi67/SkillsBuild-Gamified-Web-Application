package com.example.backend.person.csr;

import com.example.backend.person.Person;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/persons")
@Tag(name = "Person Controller", description = "API for managing persons")
public class PersonController {
    
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Person>>> getAllPersons() {
        ServiceResult<List<Person>, Void> persons = personService.getAll();
        return ResponseEntity.ok(ApiResponse.success(persons.getData()));
    }
}
