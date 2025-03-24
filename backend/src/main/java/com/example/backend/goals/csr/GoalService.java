package com.example.backend.goals.csr;

import com.example.backend.goals.Goal;
import com.example.backend.goals.GoalProgressDTO;
import org.springframework.stereotype.Service;
import com.example.backend.person.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    // Create a new goal
    public Goal createGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    // Get all goals for a person
    public List<Goal> getGoalsByPerson(Person person) {
        return goalRepository.findByPerson(person);
    }

    // Get a specific goal by ID
    public Optional<Goal> getGoalById(Long id) {
        return goalRepository.findById(id);
    }

    // Delete a goal
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    // Add courses to a goal
    public Goal addCoursesToGoal(Long goalId, Map<Integer, Boolean> courses) {
        Optional<Goal> goalOptional = goalRepository.findById(goalId);
        if (goalOptional.isPresent()) {
            Goal goal = goalOptional.get();
            courses.forEach(goal::addCourse);
            return goalRepository.save(goal);
        }
        return null;
    }

    // Update a course completion status
    public Goal updateCourseStatus(Long goalId, Integer courseId, Boolean completed) {
        Optional<Goal> goalOptional = goalRepository.findById(goalId);
        if (goalOptional.isPresent()) {
            Goal goal = goalOptional.get();
            goal.updateCourseStatus(courseId, completed);

            // Check if all courses are completed
            boolean allCoursesCompleted = goal.getAllCourses().values().stream()
                    .allMatch(status -> status);

            if (allCoursesCompleted) {
                goalRepository.deleteById(goalId);
                return null; // Goal was deleted
            }

            return goalRepository.save(goal);
        }
        return null;
    }

    // Get goal progress (percentage of completed courses)
    public double getGoalProgress(Long goalId) {
        Optional<Goal> goalOptional = goalRepository.findById(goalId);
        if (goalOptional.isPresent()) {
            Goal goal = goalOptional.get();
            Map<Integer, Boolean> courses = goal.getAllCourses();

            if (courses.isEmpty()) {
                return 0.0;
            }

            long completedCount = courses.values().stream()
                    .filter(status -> status)
                    .count();

            return (double) completedCount / courses.size() * 100;
        }
        return 0.0;
    }

    // Get all goals with their progress
    public List<GoalProgressDTO> getAllGoalsWithProgress(Person person) {
        List<Goal> goals = goalRepository.findByPerson(person);

        return goals.stream()
                .map(goal -> {
                    Map<Integer, Boolean> courses = goal.getAllCourses();
                    long completedCount = courses.values().stream()
                            .filter(status -> status)
                            .count();
                    double progress = courses.isEmpty() ? 0.0 :
                            (double) completedCount / courses.size() * 100;

                    return new GoalProgressDTO(
                            goal.getId(),
                            goal.getDescription(),
                            goal.getStartDate(),
                            goal.getEndDate(),
                            goal.getReward(),
                            progress,
                            goal.getAllCourses()
                    );
                })
                .collect(Collectors.toList());
    }

    // Scheduled task to delete expired goals (runs daily at midnight)
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredGoals() {
        LocalDate currentDate = LocalDate.now();
        List<Goal> goals = goalRepository.findByEndDateBefore(currentDate);
        goalRepository.deleteAll(goals);
    }
}