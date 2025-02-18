package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    private String link;
    private Duration estimatedDuration;
    private String difficulty;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();

    public Course(String title, String description, String link, Duration estimatedDuration, String difficulty) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.estimatedDuration = estimatedDuration;
        this.difficulty = difficulty;
    }
}

