package com.example.backend.person;

import com.example.backend.course.Course;
import com.example.backend.badge.Badge;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String avatarLink = "";
    private LocalDate lastLoginDate = LocalDate.now();

    @ManyToMany
    @JoinTable(
            name = "person_favorite_courses",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> favoriteCourses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "person_badges",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "badge_id")
    )
    private Set<Badge> badges = new HashSet<>();

    // Constructor for creating a new person for signup
    public Person(String username, String password) {
        this.username = username;
        this.password = password;
        this.streak = 1;
        this.lastLoginDate = LocalDate.now();
        this.badges = new HashSet<>();
    }

    // Constructor for creating a new person mostly for testing
    public Person(String username, String password, Integer streak, LocalDate lastLoginDate) {
        this.username = username;
        this.password = password;
        this.streak = streak;
        this.lastLoginDate = lastLoginDate;
        this.badges = new HashSet<>();
    }

    // Constructor for creating a new person due to update profile
    public Person(String username, String password, String firstName, String lastName, String email, String avatarLink) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarLink = avatarLink;
        this.badges = new HashSet<>();
    }
}
