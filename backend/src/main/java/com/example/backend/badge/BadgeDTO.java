package com.example.backend.badge;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BadgeDTO {
    private String name;
    private String description;
    private String imageUrl;
    private String criteriaType;
    private Integer criteriaValue;
} 