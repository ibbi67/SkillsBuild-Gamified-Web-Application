package com.example.backend.course;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseDTO {
    private String title;
    private String description;
    private String link;
    private Integer estimatedDuration;
    private Integer difficulty;
}
