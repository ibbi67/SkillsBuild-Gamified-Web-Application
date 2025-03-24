package com.example.backend.course;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String title;

    @Lob
    private String description;
    private String link;
    private Integer estimatedDuration;
    private Integer difficulty;

    private Integer views = 0;

    public Course(String title, String description, String link, Integer estimatedDuration, Integer difficulty) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.estimatedDuration = estimatedDuration;
        this.difficulty = difficulty;
        this.views = 0;
    }

    public Course(String title, String description, String link, Integer estimatedDuration, Integer difficulty, int i) {
    }
}
