package com.example.backend.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class GoalDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String reward;
    private boolean achieved;
}
