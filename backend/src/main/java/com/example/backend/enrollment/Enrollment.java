package com.example.backend.enrollment;

import com.example.backend.course.Course;
import com.example.backend.person.Person;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Course course;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Person person;

    private Integer timeSpent = 0;
    private Boolean completed = false;

    public Enrollment(Course course, Person person) {
        this.course = course;
        this.person = person;
    }

    public boolean isCompleted() {
        return timeSpent >= course.getEstimatedDuration() || completed;
    }

    public void addTimeSpent(Integer time) {
        this.timeSpent += time;
    }
}
