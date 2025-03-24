package com.example.backend.enrollment.csr;

import com.example.backend.course.Course;
import com.example.backend.course.csr.CourseService;
import com.example.backend.enrollment.Enrollment;
import com.example.backend.enrollment.UpdateProgressDTO;
import com.example.backend.enrollment.error.EnrollmentCreateError;
import com.example.backend.enrollment.error.EnrollmentGetAllError;
import com.example.backend.enrollment.error.EnrollmentGetByIdError;
import com.example.backend.enrollment.error.EnrollmentUpdateProgressError;
import com.example.backend.person.Person;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final JWT jwt;
    private final CourseService courseService;
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(JWT jwt, CourseService courseService, EnrollmentRepository enrollmentRepository) {
        this.jwt = jwt;
        this.courseService = courseService;
        this.enrollmentRepository = enrollmentRepository;
    }

    public Optional<Enrollment> findById(Integer id) {
        return enrollmentRepository.findById(id);
    }

    public Optional<Enrollment> save(Enrollment enrollment) {
        return Optional.of(enrollmentRepository.save(enrollment));
    }

    public List<Enrollment> findByPersonId(Long personId) {
        return enrollmentRepository.findByPersonId(personId);
    }

    public ServiceResult<List<Enrollment>, EnrollmentGetAllError> getAll(String refreshToken) {
        Optional<Person> personOptional = jwt.getPersonFromToken(refreshToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(EnrollmentGetAllError.INVALID_ACCESS_TOKEN);
        }
        Person person = personOptional.get();

        List<Enrollment> enrollments = findByPersonId(person.getId());
        if (!enrollments.isEmpty()) {
            return ServiceResult.success(enrollments);
        }

        return ServiceResult.error(EnrollmentGetAllError.ENROLLMENT_NOT_FOUND);
    }

    public ServiceResult<Enrollment, EnrollmentGetByIdError> getById(Integer id) {
        if (id == null || id <= 0) {
            return ServiceResult.error(EnrollmentGetByIdError.INVALID_ID);
        }

        Optional<Enrollment> enrollmentOptional = findById(id);
        if (enrollmentOptional.isPresent()) {
            Enrollment enrollment = enrollmentOptional.get();
            return ServiceResult.success(enrollment);
        }

        return ServiceResult.error(EnrollmentGetByIdError.ENROLLMENT_NOT_FOUND);
    }

    public ServiceResult<Enrollment, EnrollmentCreateError> create(String refreshToken, Integer courseId) {
        if (courseId == null || courseId <= 0) {
            return ServiceResult.error(EnrollmentCreateError.INVALID_COURSE_ID);
        }
        Optional<Person> personOptional = jwt.getPersonFromToken(refreshToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(EnrollmentCreateError.INVALID_ACCESS_TOKEN);
        }

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (courseOptional.isEmpty()) {
            return ServiceResult.error(EnrollmentCreateError.COURSE_NOT_FOUND);
        }

        Person person = personOptional.get();
        Course course = courseOptional.get();
        Enrollment enrollment = new Enrollment(course, person);

        Optional<Enrollment> enrollmentOptional = save(enrollment);
        if (enrollmentOptional.isPresent()) {
            enrollment = enrollmentOptional.get();
            return ServiceResult.success(enrollment);
        }

        return ServiceResult.error(EnrollmentCreateError.ENROLLMENT_CREATION_FAILED);
    }

    public ServiceResult<Enrollment, EnrollmentUpdateProgressError> updateProgress(Integer enrollmentId, UpdateProgressDTO updateProgressDTO) {
        if (enrollmentId == null || enrollmentId <= 0) {
            return ServiceResult.error(EnrollmentUpdateProgressError.INVALID_ENROLLMENT_ID);
        }
        if (updateProgressDTO.getTimeSpent() == null || updateProgressDTO.getTimeSpent() <= 0) {
            return ServiceResult.error(EnrollmentUpdateProgressError.INVALID_TIME_SPENT);
        }
        Optional<Enrollment> enrollmentOptional = findById(enrollmentId);
        if (enrollmentOptional.isEmpty()) {
            return ServiceResult.error(EnrollmentUpdateProgressError.ENROLLMENT_NOT_FOUND);
        }

        Enrollment enrollment = enrollmentOptional.get();
        enrollment.addTimeSpent(updateProgressDTO.getTimeSpent());

        Optional<Enrollment> updatedEnrollmentOptional = save(enrollment);
        if (updatedEnrollmentOptional.isPresent()) {
            return ServiceResult.success(updatedEnrollmentOptional.get());
        }

        return ServiceResult.error(EnrollmentUpdateProgressError.ENROLLMENT_UPDATE_FAILED);
    }
}
