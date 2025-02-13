package com.example.backend.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private static final ObjectMapper mapper = new ObjectMapper();
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> deserialise(String json, Class<T> clazz) throws JsonProcessingException {
        return mapper.readValue(json, mapper.getTypeFactory().constructParametricType(ApiResponse.class, clazz));
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> failed(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }
}
