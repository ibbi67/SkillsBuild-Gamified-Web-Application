package com.example.backend.goals;

import com.example.backend.enrollment.Enrollment;
import com.example.backend.person.Person;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.example.backend.course.Course;
import com.example.backend.person.Person;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String reward;

    @ManyToMany
    @JoinTable(
        name = "goal_enrollment",
        joinColumns = @JoinColumn(name = "goal_id"),
        inverseJoinColumns = @JoinColumn(name = "enrollment_id")
    )
    List<Enrollment> enrollments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public Goal(LocalDate startDate, LocalDate endDate, String description, String reward, Person person) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.reward = reward;
        this.person = person;
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }
}
