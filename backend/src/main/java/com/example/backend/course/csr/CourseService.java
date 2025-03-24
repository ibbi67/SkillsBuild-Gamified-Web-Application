package com.example.backend.course.csr;

import com.example.backend.course.Course;
import com.example.backend.course.CourseDTO;
import com.example.backend.course.error.CourseCreateError;
import com.example.backend.course.error.CourseGetAllError;
import com.example.backend.course.error.CourseGetByIdError;
import com.example.backend.course.error.CourseGetRecommendError;
import com.example.backend.util.ServiceResult;
import com.example.backend.util.JWT;
import com.example.backend.person.Person;
import org.springframework.stereotype.Service;
import com.example.backend.course.error.CourseGetTrendingError;
import com.example.backend.course.error.CourseViewError;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final JWT jwt;

    public CourseService(JWT jwt, CourseRepository courseRepository) {
        this.jwt = jwt;
        this.courseRepository = courseRepository;
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Optional<Course> findById(Integer id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> save(Course course) {
        try {
            Course savedCourse = courseRepository.save(course);
            return Optional.of(savedCourse);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public ServiceResult<List<Course>, CourseGetAllError> getAll() {
        List<Course> courses = findAll();
        if (!courses.isEmpty()) {
            return ServiceResult.success(courses);
        }

        return ServiceResult.error(CourseGetAllError.GET_ALL_COURSES_FAILED);
    }

    public ServiceResult<Course, CourseGetByIdError> getById(Integer id) {
        if (id == null || id <= 0) {
            return ServiceResult.error(CourseGetByIdError.GET_COURSE_BY_ID_FAILED);
        }

        Optional<Course> courseOptional = findById(id);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            return ServiceResult.success(course);
        }

        return ServiceResult.error(CourseGetByIdError.COURSE_NOT_FOUND);
    }

    public ServiceResult<List<Course>, CourseGetTrendingError> getTrendingCourses() {
        try {
            List<Course> trendingCourses = courseRepository.findTop10ByOrderByViewsDesc();
            return ServiceResult.success(trendingCourses);
        } catch (Exception e) {
            return ServiceResult.error(CourseGetTrendingError.GET_TRENDING_COURSES_FAILED);
        }
    }

    public ServiceResult<Void, CourseViewError> incrementCourseViews(Integer id) {
        if (id == null || id <= 0) {
            return ServiceResult.error(CourseViewError.COURSE_NOT_FOUND);
        }

        Optional<Course> courseOptional = findById(id);
        if (courseOptional.isEmpty()) {
            return ServiceResult.error(CourseViewError.COURSE_NOT_FOUND);
        }

        try {
            Course course = courseOptional.get();
            course.setViews(course.getViews() + 1);
            save(course);
            return ServiceResult.success(null);
        } catch (Exception e) {
            return ServiceResult.error(CourseViewError.VIEW_INCREMENT_FAILED);
        }
    }

    public ServiceResult<Course, CourseCreateError> create(CourseDTO courseDTO) {
        Course course = new Course(courseDTO.getTitle(), courseDTO.getDescription(), courseDTO.getLink(), courseDTO.getEstimatedDuration(), courseDTO.getDifficulty(), 0);
        Optional<Course> savedCourse = save(course);
        if (savedCourse.isPresent()) {
            course = savedCourse.get();
            return ServiceResult.success(course);
        }
        return ServiceResult.error(CourseCreateError.COURSE_CREATION_FAILED);
    }

    public ServiceResult<List<Course>, CourseGetRecommendError> getRecommendedCourses(String accessToken) {
        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(CourseGetRecommendError.INVALID_ACCESS_TOKEN);
        }

        Person person = personOptional.get();
        List<Course> favoriteCourses = person.getFavoriteCourses();
        if (favoriteCourses.isEmpty()) {
            return ServiceResult.success(List.of());
        }

        int totalDifficulty = favoriteCourses.stream().mapToInt(Course::getDifficulty).sum();
        double averageDifficulty = (double) totalDifficulty / favoriteCourses.size();
        int minDifficulty = (int) Math.max(averageDifficulty - 1, 0);
        int maxDifficulty = (int) Math.min(averageDifficulty + 1, 5);

        List<Course> recommendedCourses = courseRepository.findAll().stream()
                .filter(course -> course.getDifficulty() >= minDifficulty && course.getDifficulty() <= maxDifficulty)
                .collect(Collectors.toList());
        recommendedCourses.removeIf(favoriteCourses::contains);

        if (recommendedCourses.isEmpty()) {
            return ServiceResult.success(List.of());
        }

        return ServiceResult.success(recommendedCourses);
    }
}
