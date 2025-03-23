package com.example.backend.favourite.csr;

import com.example.backend.course.Course;
import com.example.backend.course.csr.CourseService;
import com.example.backend.favourite.error.FavouriteCreateError;
import com.example.backend.favourite.error.FavouriteGetAllError;
import com.example.backend.favourite.error.FavouriteRemoveError;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FavouriteService {

    private final JWT jwt;
    private final PersonService personService;
    private final CourseService courseService;

    public FavouriteService(JWT jwt, PersonService personService, CourseService courseService) {
        this.jwt = jwt;
        this.personService = personService;
        this.courseService = courseService;
    }

    public ServiceResult<List<Course>, FavouriteGetAllError> getAll(String refreshToken) {
        Optional<Person> personOptional = jwt.getPersonFromToken(refreshToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(FavouriteGetAllError.INVALID_ACCESS_TOKEN);
        }
        Person person = personOptional.get();
        List<Course> favoriteCourses = person.getFavoriteCourses();
        if (favoriteCourses == null || favoriteCourses.isEmpty()) {
            return ServiceResult.success(List.of());
        }
        return ServiceResult.success(favoriteCourses);
    }

    public ServiceResult<Void, FavouriteCreateError> create(String refreshToken, Integer courseId) {
        Optional<Person> personOptional = jwt.getPersonFromToken(refreshToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(FavouriteCreateError.INVALID_ACCESS_TOKEN);
        }
        Person person = personOptional.get();
        Optional<Course> courseOptional = courseService.findById(courseId);
        if (courseOptional.isEmpty()) {
            return ServiceResult.error(FavouriteCreateError.COURSE_NOT_FOUND);
        }
        Course course = courseOptional.get();
        if (personService.addFavouriteCourse(person, course).isEmpty()) {
            return ServiceResult.error(FavouriteCreateError.COURSE_ALREADY_FAVORITE);
        }
        return ServiceResult.success(null);
    }

    public ServiceResult<Void, FavouriteRemoveError> remove(String refreshToken, Integer courseId) {
        Optional<Person> personOptional = jwt.getPersonFromToken(refreshToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(FavouriteRemoveError.INVALID_ACCESS_TOKEN);
        }
        Person person = personOptional.get();
        Optional<Course> courseOptional = courseService.findById(courseId);
        if (courseOptional.isEmpty()) {
            return ServiceResult.error(FavouriteRemoveError.COURSE_NOT_FOUND);
        }
        Course course = courseOptional.get();
        if (personService.removeFavouriteCourse(person, course).isEmpty()) {
            return ServiceResult.error(FavouriteRemoveError.COURSE_NOT_FAVORITE);
        }
        return ServiceResult.success(null);
    }
}
