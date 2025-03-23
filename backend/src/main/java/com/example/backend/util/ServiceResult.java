package com.example.backend.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServiceResult<T, E> {
    private T data;
    private E error;

    public static <T, E> ServiceResult<T, E> success(T data) {
        return new ServiceResult<>(data, null);
    }

    public static <T, E> ServiceResult<T, E> error(E error) {
        return new ServiceResult<>(null, error);
    }

    public boolean isSuccess() {
        return error == null;
    }
}
