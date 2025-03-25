package com.example.backend.goals.csr;

import com.example.backend.enrollment.Enrollment;
import com.example.backend.enrollment.csr.EnrollmentService;
import com.example.backend.goals.AddEnrollmentDTO;
import com.example.backend.goals.Goal;
import com.example.backend.goals.GoalDTO;
import com.example.backend.goals.error.*;
import com.example.backend.person.Person;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    private final JWT jwt;
    private final GoalRepository goalRepository;
    private final EnrollmentService enrollmentService;

    public GoalService(JWT jwt, GoalRepository goalRepository, EnrollmentService enrollmentService) {
        this.jwt = jwt;
        this.goalRepository = goalRepository;
        this.enrollmentService = enrollmentService;
    }

    public Optional<Goal> save(Goal goal) {
        return Optional.of(goalRepository.save(goal));
    }

    public Optional<Goal> save(GoalDTO goalDTO, Person person) {
        Goal goal = new Goal(goalDTO.getStartDate(), goalDTO.getEndDate(), goalDTO.getDescription(), goalDTO.getReward(), person);
        return Optional.of(goalRepository.save(goal));
    }

    public void deleteById(Long id) {
        goalRepository.deleteById(id);
    }

    public Optional<Goal> findById(Long id) {
        return goalRepository.findById(id);
    }

    public List<Goal> findByPersonId(Long personId) {
        return goalRepository.findByPersonId(personId);
    }

    public ServiceResult<Void, GoalCreateError> createGoal(String accessToken, GoalDTO goalDTO) {
        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(GoalCreateError.INVALID_ACCESS_TOKEN);
        }
        Person person = personOptional.get();
        Optional<Goal> goal = save(goalDTO, person);
        if (goal.isEmpty()) {
            return ServiceResult.error(GoalCreateError.FAILED_TO_CREATE_GOAL);
        }
        return ServiceResult.success(null);
    }

    public ServiceResult<Void, GoalDeleteError> deleteGoal(String accessToken, Long goalId) {
        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(GoalDeleteError.INVALID_ACCESS_TOKEN);
        }
        Person person = personOptional.get();
        Optional<Goal> goal = goalRepository.findById(goalId);
        if (goal.isEmpty()) {
            return ServiceResult.error(GoalDeleteError.GOAL_NOT_FOUND);
        }
        if (!goal.get().getPerson().equals(person)) {
            return ServiceResult.error(GoalDeleteError.PERMISSION_DENIED);
        }
        deleteById(goalId);
        return ServiceResult.success(null);
    }

    public ServiceResult<Goal, GoalAddEnrollmentError> addEnrollmentToGoal(Long goalId, AddEnrollmentDTO addEnrollmentDTO) {
        Optional<Goal> goalOptional = goalRepository.findById(goalId);
        if (goalOptional.isEmpty()) {
            return ServiceResult.error(GoalAddEnrollmentError.GOAL_NOT_FOUND);
        }
        Goal goal = goalOptional.get();
        List<Enrollment> enrollments = addEnrollmentDTO.getEnrollmentIds().stream().map(enrollmentService::findById).filter(Optional::isPresent).map(Optional::get).toList();
        if (enrollments.isEmpty()) {
            return ServiceResult.error(GoalAddEnrollmentError.ENROLLMENTS_NOT_FOUND);
        }
        enrollments.forEach(goal::addEnrollment);
        save(goal);
        return ServiceResult.success(goal);
    }

    public ServiceResult<List<Goal>, GoalGetError> getGoals(String accessToken) {
        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(GoalGetError.INVALID_ACCESS_TOKEN);
        }
        Person person = personOptional.get();
        List<Goal> goals = findByPersonId(person.getId());
        return ServiceResult.success(goals);
    }

    // Toggle the completion status of an enrollment
    public ServiceResult<Goal, GoalUpdateEnrollmentCompletionStatusError> updateEnrollmentCompletionStatus(String accessToken, Long goalId, Integer enrollmentId) {
        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(GoalUpdateEnrollmentCompletionStatusError.INVALID_ACCESS_TOKEN);
        }
        Person person = personOptional.get();
        Optional<Goal> goalOptional = findById(goalId);
        if (goalOptional.isEmpty()) {
            return ServiceResult.error(GoalUpdateEnrollmentCompletionStatusError.GOAL_NOT_FOUND);
        }
        Goal goal = goalOptional.get();
        if (!goal.getPerson().equals(person)) {
            return ServiceResult.error(GoalUpdateEnrollmentCompletionStatusError.PERMISSION_DENIED);
        }
        Optional<Enrollment> enrollmentOptional = enrollmentService.findById(enrollmentId);
        if (enrollmentOptional.isEmpty()) {
            return ServiceResult.error(GoalUpdateEnrollmentCompletionStatusError.ENROLLMENTS_NOT_FOUND);
        }
        Enrollment enrollment = enrollmentOptional.get();
        enrollment.setCompleted(!enrollment.getCompleted());
        save(goal);
        return ServiceResult.success(goal);
    }
}