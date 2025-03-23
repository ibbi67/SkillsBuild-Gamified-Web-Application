package com.example.backend.person;

import com.example.backend.course.Course;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private Integer streak = 1;
    private LocalDate lastLoginDate = LocalDate.now();

    @ManyToMany
    @JoinTable(
            name = "person_favorite_courses",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> favoriteCourses = new ArrayList<>();

    public Person(String username, String password) {
        this.username = username;
        this.password = password;
        this.streak = 1;
        this.lastLoginDate = LocalDate.now();
    }

    public Person(String username, String password, Integer streak, LocalDate lastLoginDate) {
        this.username = username;
        this.password = password;
        this.streak = streak;
        this.lastLoginDate = lastLoginDate;
    }
}
