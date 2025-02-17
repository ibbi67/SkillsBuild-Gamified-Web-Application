package com.example.backend.service;

import com.example.backend.dao.LoginDao;
import com.example.backend.dao.SignupDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Streak;
import com.example.backend.domain.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class AuthService {

    private final UserService userService;
    private final StreaksService streaksService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthService(UserService userService, StreaksService streaksService, JwtService jwtService) {
        this.userService = userService;
        this.streaksService = streaksService;
        this.jwtService = jwtService;
    }

    public ApiResponse<Void> signup(SignupDao signupDao, HttpServletResponse response) {
        User user = new User();
        user.setUsername(signupDao.getUsername());
        user.setPassword(signupDao.getPassword());
        user.getStreak().setUser(user);

        user = userService.save(user);
        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "User already exists");

        response.addCookie(jwtService.generateRefreshTokenCookie(user.getUsername()));
        response.addCookie(jwtService.generateAccessTokenCookie(user.getUsername()));

        checkAndUpdateStreak(user);

        return ApiResponse.success("User created successfully");
    }

    public ApiResponse<Void> login(LoginDao loginDao, HttpServletResponse response) {
        User user = userService.findByUsername(loginDao.getUsername());

        // We should not disclose whether the user exists or not due to security reasons
        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid credentials");

        boolean passwordMatch = bCryptPasswordEncoder.matches(loginDao.getPassword(), user.getPassword());
        if (!passwordMatch) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid credentials");

        response.addCookie(jwtService.generateRefreshTokenCookie(user.getUsername()));
        response.addCookie(jwtService.generateAccessTokenCookie(user.getUsername()));

        checkAndUpdateStreak(user);

        return ApiResponse.success("Login successful");
    }

    public ApiResponse<Void> refresh(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid refresh token");
        if (!jwtService.verifyToken(refreshToken)) return ApiResponse.failed(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid refresh token"
        );

        String username = jwtService.getUserDetails(refreshToken).getUsername();
        User user = userService.findByUsername(username);
        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid refresh token");

        response.addCookie(jwtService.generateAccessTokenCookie(username));
        
        checkAndUpdateStreak(user);

        return ApiResponse.success("Token refreshed");
    }

    private void checkAndUpdateStreak(User user) {
        Streak streak = user.getStreak();
        LocalDate today = LocalDate.now();
        LocalDate previousLoginDate = streak.getPreviousLogin() != null ?
                streak.getPreviousLogin().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                : null;
                
        // Only update if not already logged in today
        if (previousLoginDate == null || !previousLoginDate.equals(today)) {
            LocalDate yesterday = today.minusDays(1);
            
            if (previousLoginDate != null && previousLoginDate.equals(yesterday)) {
                // If last login was yesterday, increment streak
                streak.setStreak(streak.getStreak() + 1);
            } else {
                // If last login was not yesterday, reset streak to 1
                streak.setStreak(1);
            }
            
            streak.setPreviousLogin(Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            userService.save(user);
            streaksService.saveStreak(streak);
        }
    }

    public ApiResponse<Void> logout(HttpServletResponse response) {
        response.addCookie(jwtService.generateLogoutAccessCookie());
        response.addCookie(jwtService.generateLogoutRefreshCookie());
        return ApiResponse.success("Logout successful");
    }
}
