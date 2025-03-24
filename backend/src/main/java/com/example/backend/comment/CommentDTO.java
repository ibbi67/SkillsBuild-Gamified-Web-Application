package com.example.backend.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @NotBlank(message = "Comment content cannot be empty")
    private String content;

    @NotNull(message = "Course ID cannot be null")
    private Integer courseId;
}