package com.example.backend.service;

import com.example.backend.dao.LoginDao;
import com.example.backend.dao.SignupDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public ApiResponse<Void> signup(SignupDao signupDao) {
        User user = new User();
        user.setUsername(signupDao.getUsername());
        user.setPassword(signupDao.getPassword());

        user = userService.save(user);
        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "User already exists");

        return ApiResponse.success("User created successfully");
    }

    public ApiResponse<Void> login(LoginDao loginDao) {
        User user = userService.findByUsername(loginDao.getUsername());

        // We should not disclose whether the user exists or not due to security reasons
        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid credentials");

        boolean passwordMatch = bCryptPasswordEncoder.matches(loginDao.getPassword(), user.getPassword());
        if (!passwordMatch) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid credentials");

        return ApiResponse.success("Login successful");
    }
}
