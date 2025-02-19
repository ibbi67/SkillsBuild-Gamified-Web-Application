package com.example.backend.dao;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FavouriteCourseDao {
    @NotBlank
    private Integer courseId;
}
