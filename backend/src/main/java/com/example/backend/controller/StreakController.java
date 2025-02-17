package com.example.backend.controller;

import com.example.backend.domain.Streak;
import com.example.backend.domain.User;
import com.example.backend.repository.StreakRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/streak")
public class StreakController {
    private final StreakRepository streakRepository;
    private final UserRepository userRepository;

    public StreakController(StreakRepository streakRepository, UserRepository userRepository) {
        this.streakRepository = streakRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Streak> getStreak(@PathVariable int userId) {
        Optional<Streak> streak = streakRepository.findByUserId(userId);
        return streak.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
