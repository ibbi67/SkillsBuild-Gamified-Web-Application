package com.example.backend.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("Success", data);
    }

    public static <T> ApiResponse<T> failed(String message) {
        return new ApiResponse<>(message, null);
    }
}