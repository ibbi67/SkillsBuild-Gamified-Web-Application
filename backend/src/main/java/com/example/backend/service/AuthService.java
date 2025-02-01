package com.example.backend.service;

import com.example.backend.dao.SignupDao;
import com.example.backend.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UserService userService;

    @Autowired
    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User signup(SignupDao signupDao) {
        User user = new User();
        user.setUsername(signupDao.getUsername());
        user.setPassword(signupDao.getPassword());

        return userService.save(user);
    }
}
