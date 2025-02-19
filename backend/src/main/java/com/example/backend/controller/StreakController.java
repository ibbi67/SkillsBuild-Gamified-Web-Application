package com.example.backend.controller;

import com.example.backend.domain.Streak;
import com.example.backend.repository.StreakRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/streak")
public class StreakController {
    private final StreakRepository streakRepository;

    public StreakController(StreakRepository streakRepository) {
        this.streakRepository = streakRepository;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Streak> getStreak(@PathVariable int userId) {
        Optional<Streak> streak = streakRepository.findByUserId(userId);
        return streak.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
