package com.example.backend.service;

import com.example.backend.domain.Streak;
import com.example.backend.repository.StreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StreaksService {
    private final StreakRepository streakRepository;
    @Autowired
    public StreaksService(StreakRepository streakRepository) {
        this.streakRepository = streakRepository;
    }

    public Streak findByUserId(Integer userId){
        return streakRepository.findByUserId(userId);
    }

    public Integer incrementStreak(Streak streak){
        Streak currentStreak = streak;
        currentStreak.setStreak(currentStreak.getStreak() + 1);
        streakRepository.save(currentStreak);
        return currentStreak.getStreak();
    }
}
