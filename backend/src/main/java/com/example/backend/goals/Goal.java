package com.example.backend.goals;

import com.example.backend.person.Person;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.backend.course.Course;
import com.example.backend.person.Person;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate; // Start date of the goal
    private LocalDate endDate; // End date of the goal
    private String description;
    private String reward;
    private boolean achieved;

    // Replace CourseID with a HashMap to store multiple courses and their completion status
    @ElementCollection
    @CollectionTable(name = "goal_courses", joinColumns = @JoinColumn(name = "goal_id"))
    @MapKeyColumn(name = "course_id")
    @Column(name = "completed")
    private Map<Integer, Boolean> courses = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "person_id")
    @JsonBackReference  // This prevents infinite recursion
    private Person person;

    public Goal(LocalDate startDate, LocalDate endDate, String description, String reward, boolean achieved) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.reward = reward;
        this.achieved = achieved;
        this.courses = new HashMap<>();
    }

    // Add a course with its completion status
    public void addCourse(Integer courseId, Boolean completed) {
        this.courses.put(courseId, completed);
    }

    // Update a course's completion status
    public void updateCourseStatus(Integer courseId, Boolean completed) {
        if (this.courses.containsKey(courseId)) {
            this.courses.put(courseId, completed);
        }
    }

    // Check if a specific course is completed
    public Boolean isCourseCompleted(Integer courseId) {
        return this.courses.getOrDefault(courseId, false);
    }

    // Get all courses with their completion status
    public Map<Integer, Boolean> getAllCourses() {
        return this.courses;
    }
}
