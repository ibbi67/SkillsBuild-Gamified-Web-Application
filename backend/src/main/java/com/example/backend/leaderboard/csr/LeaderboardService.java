package com.example.backend.leaderboard.csr;

import com.example.backend.enrollment.Enrollment;
import com.example.backend.enrollment.csr.EnrollmentService;
import com.example.backend.leaderboard.LeaderboardDTO;
import com.example.backend.leaderboard.error.LeaderboardGetAllError;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.util.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private final PersonService personService;
    private final EnrollmentService enrollmentService;

    public LeaderboardService(PersonService personService, EnrollmentService enrollmentService) {
        this.personService = personService;
        this.enrollmentService = enrollmentService;
    }

    public ServiceResult<List<LeaderboardDTO>, LeaderboardGetAllError> getAll() {
        List<Person> persons = personService.findAll();
        if (persons.isEmpty()) {
            return ServiceResult.error(LeaderboardGetAllError.LEADERBOARD_NOT_FOUND);
        }

        List<LeaderboardDTO> leaderboard = persons.stream()
                .map(person -> new LeaderboardDTO(person.getUsername(), calculatePoints(person)))
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .toList();

        return ServiceResult.success(leaderboard);
    }

    private Integer calculatePoints(Person person) {
        List<Enrollment> enrollments = enrollmentService.findByPersonId(person.getId());

        int completedCourses = enrollments.stream()
                .filter(Enrollment::isCompleted)
                .mapToInt(enrollment -> enrollment.getCourse().getDifficulty())
                .sum();

        double averageDifficulty = enrollments.stream()
                .mapToInt(enrollment -> enrollment.getCourse().getDifficulty())
                .average()
                .orElse(0);

        int streakDays = person.getStreak();
        int enrollmentsCount = enrollments.size();

        return (int) ((completedCourses * averageDifficulty * 20) + (streakDays * 10) + (enrollmentsCount * 5));
    }
}
