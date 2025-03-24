package com.example.backend.goals.csr;

import com.example.backend.goals.Goal;
import com.example.backend.goals.GoalProgressDTO;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @Autowired
    private PersonService personService;

    // Create a new goal
    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal, @RequestParam Long personId) {
        Optional<Person> personOptional = personService.getPersonById(personId);
        if (personOptional.isPresent()) {
            goal.setPerson(personOptional.get());
            Goal savedGoal = goalService.createGoal(goal);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGoal);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Get all goals for a person
    @GetMapping("/person/{personId}")
    public ResponseEntity<List<Goal>> getGoalsByPerson(@PathVariable Long personId) {
        Optional<Person> personOptional = personService.getPersonById(personId);
        if (personOptional.isPresent()) {
            List<Goal> goals = goalService.getGoalsByPerson(personOptional.get());
            return ResponseEntity.ok(goals);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Get a specific goal
    @GetMapping("/{goalId}")
    public ResponseEntity<Goal> getGoalById(@PathVariable Long goalId) {
        Optional<Goal> goalOptional = goalService.getGoalById(goalId);
        return goalOptional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Delete a goal
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }

    // Add courses to a goal
    @PostMapping("/{goalId}/courses")
    public ResponseEntity<Goal> addCoursesToGoal(
            @PathVariable Long goalId,
            @RequestBody Map<Integer, Boolean> courses) {
        Goal updatedGoal = goalService.addCoursesToGoal(goalId, courses);
        if (updatedGoal != null) {
            return ResponseEntity.ok(updatedGoal);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Update a course completion status
    @PutMapping("/{goalId}/courses/{courseId}")
    public ResponseEntity<?> updateCourseStatus(
            @PathVariable Long goalId,
            @PathVariable Integer courseId,
            @RequestParam Boolean completed) {
        Goal updatedGoal = goalService.updateCourseStatus(goalId, courseId, completed);
        if (updatedGoal != null) {
            return ResponseEntity.ok(updatedGoal);
        } else {
            // Check if the goal was deleted (all courses completed)
            Optional<Goal> goalOptional = goalService.getGoalById(goalId);
            if (goalOptional.isEmpty()) {
                return ResponseEntity.ok().body("Goal completed and removed");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // Get goal progress
    @GetMapping("/{goalId}/progress")
    public ResponseEntity<Double> getGoalProgress(@PathVariable Long goalId) {
        double progress = goalService.getGoalProgress(goalId);
        return ResponseEntity.ok(progress);
    }

    // Get all goals with their progress for dashboard
    @GetMapping("/dashboard/{personId}")
    public ResponseEntity<List<GoalProgressDTO>> getGoalsForDashboard(@PathVariable Long personId) {
        Optional<Person> personOptional = personService.getPersonById(personId);
        if (personOptional.isPresent()) {
            List<GoalProgressDTO> goalsWithProgress = goalService.getAllGoalsWithProgress(personOptional.get());
            return ResponseEntity.ok(goalsWithProgress);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}