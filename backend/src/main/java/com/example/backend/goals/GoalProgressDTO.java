package com.example.backend.goals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalProgressDTO {
    private Long id;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reward;
    private double progress; // Progress percentage (0-100)
    private Map<Integer, Boolean> courses; // Course IDs and their completion status
}