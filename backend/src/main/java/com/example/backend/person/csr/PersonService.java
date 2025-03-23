package com.example.backend.person.csr;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.backend.course.Course;
import com.example.backend.person.Person;
import com.example.backend.person.PersonDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Optional<Person> findByUsername(String username) {
        return personRepository.findByUsername(username);
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> save(Person person) {
        return Optional.of(personRepository.save(person));
    }

    public Optional<Person> saveNewPerson(PersonDTO personDTO) {
        Person person = new Person(personDTO.getUsername(), BCrypt.withDefaults().hashToString(12, personDTO.getPassword().toCharArray()));
        return save(person);
    }

    public Optional<Person> verifyPassword(PersonDTO personDTO) {
        Optional<Person> personOptional = findByUsername(personDTO.getUsername());
        if (personOptional.isEmpty()) {
            return Optional.empty();
        }

        Person person = personOptional.get();
        BCrypt.Result result = BCrypt.verifyer().verify(personDTO.getPassword().toCharArray(), person.getPassword());
        return result.verified ? Optional.of(person) : Optional.empty();
    }

    public Optional<Person> addFavouriteCourse(Person person, Course course) {
        if (person.getFavoriteCourses().contains(course)) {
            return Optional.empty();
        }
        person.getFavoriteCourses().add(course);
        return save(person);
    }

    public Optional<Person> removeFavouriteCourse(Person person, Course course) {
        if (!person.getFavoriteCourses().contains(course)) {
            return Optional.empty();
        }
        person.getFavoriteCourses().remove(course);
        return save(person);
    }
}
