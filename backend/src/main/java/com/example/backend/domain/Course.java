package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;

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

    private Long estimatedDurationInSeconds; // Store as seconds, so we can easily work with it

    public Course(String title, String description, String link, Duration estimatedDuration) {
        this.title = title;
        this.description = description;
        this.link = link;
    }

    
}


