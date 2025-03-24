package com.example.backend.favourite;

import com.example.backend.course.Course;
import com.example.backend.course.csr.CourseService;
import com.example.backend.favourite.csr.FavouriteService;
import com.example.backend.favourite.error.FavouriteCreateError;
import com.example.backend.favourite.error.FavouriteGetAllError;
import com.example.backend.favourite.error.FavouriteRemoveError;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class FavouriteServiceTest {

    @Mock
    private JWT jwt;

    @Mock
    private PersonService personService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private FavouriteService favouriteService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllFavourites() {
        // Mocking
        Person person = new Person();
        when(jwt.getPersonFromToken("validToken")).thenReturn(Optional.of(person));

        // Test
        ServiceResult<List<Course>, FavouriteGetAllError> result = favouriteService.getAll("validToken");
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    public void testCreateFavourite() {
        // Mocking
        Person person = new Person();
        Course course = new Course();
        when(jwt.getPersonFromToken("validToken")).thenReturn(Optional.of(person));
        when(courseService.findById(1)).thenReturn(Optional.of(course));
        when(personService.addFavouriteCourse(any(Person.class), any(Course.class))).thenReturn(Optional.of(person));

        // Test
        ServiceResult<Void, FavouriteCreateError> result = favouriteService.create("validToken", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testRemoveFavourite() {
        // Mocking
        Person person = new Person();
        Course course = new Course();
        when(jwt.getPersonFromToken("validToken")).thenReturn(Optional.of(person));
        when(courseService.findById(1)).thenReturn(Optional.of(course));
        when(personService.removeFavouriteCourse(any(Person.class), any(Course.class))).thenReturn(Optional.of(person));

        // Test
        ServiceResult<Void, FavouriteRemoveError> result = favouriteService.remove("validToken", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetAllFavouritesInvalidToken() {
        // Mocking
        when(jwt.getPersonFromToken("invalidToken")).thenReturn(Optional.empty());

        // Test
        ServiceResult<List<Course>, FavouriteGetAllError> result = favouriteService.getAll("invalidToken");
        assertFalse(result.isSuccess());
        assertNull(result.getData());
        assertEquals(FavouriteGetAllError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    public void testCreateFavouriteInvalidToken() {
        // Mocking
        when(jwt.getPersonFromToken("invalidToken")).thenReturn(Optional.empty());

        // Test
        ServiceResult<Void, FavouriteCreateError> result = favouriteService.create("invalidToken", 1);
        assertFalse(result.isSuccess());
        assertEquals(FavouriteCreateError.INVALID_ACCESS_TOKEN, result.getError());
    }

    @Test
    public void testRemoveFavouriteInvalidToken() {
        // Mocking
        when(jwt.getPersonFromToken("invalidToken")).thenReturn(Optional.empty());

        // Test
        ServiceResult<Void, FavouriteRemoveError> result = favouriteService.remove("invalidToken", 1);
        assertFalse(result.isSuccess());
        assertEquals(FavouriteRemoveError.INVALID_ACCESS_TOKEN, result.getError());
    }
}
