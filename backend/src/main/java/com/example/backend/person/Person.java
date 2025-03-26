package com.example.backend.person;

import com.example.backend.course.Course;
import com.example.backend.badge.Badge;
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
    private List<Badge> badges = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "person_friends",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Person> friends = new ArrayList<>();

    public Person(String username, String password) {
        this.username = username;
        this.password = password;
        this.streak = 1;
        this.lastLoginDate = LocalDate.now();
    }

    // Constructor for creating a new person mostly for testing
    public Person(String username, String password, Integer streak, LocalDate lastLoginDate) {
        this.username = username;
        this.password = password;
        this.streak = streak;
        this.lastLoginDate = lastLoginDate;
    }

    // Constructor for creating a new person due to update profile
    public Person(String username, String password, String firstName, String lastName, String email, String avatarLink) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarLink = avatarLink;
    }

    public Person(String username, String password, Integer streak, String firstName, String lastName, String email, String avatarLink) {
        this.username = username;
        this.password = password;
        this.streak = streak;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatarLink = avatarLink;
    }
}
