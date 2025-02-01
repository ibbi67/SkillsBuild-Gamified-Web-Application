package com.example.backend.dao;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class SignupDao {
    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
