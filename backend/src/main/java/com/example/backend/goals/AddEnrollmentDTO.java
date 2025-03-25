package com.example.backend.goals;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AddEnrollmentDTO {
    List<Integer> enrollmentIds;
}
