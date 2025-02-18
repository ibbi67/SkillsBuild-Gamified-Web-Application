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

    private String title;

    @Lob
    private String description;
    private String link;
    private Duration estimatedDuration;

    public Course(String title, String description, String link, Duration estimatedDuration) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.estimatedDuration = estimatedDuration;
    }
}
