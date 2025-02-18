package com.example.backend.service;

import com.example.backend.domain.Streak;
import com.example.backend.repository.StreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StreaksService {
    private final StreakRepository streakRepository;
    @Autowired
    public StreaksService(StreakRepository streakRepository) {
        this.streakRepository = streakRepository;
    }

    public Streak getStreakById(int id) {
        return streakRepository.findById(id).orElse(null);
    }

//    public Streak findByUserId(Integer userId){
//        return streakRepository.findByUserId(userId);
//    }

    public void checkMaintainedStreak(){
        ///here we need to check if the user has logged in yesterday or if the streak has been broken
    }

    public Streak saveStreak(Streak streak){
        streakRepository.save(streak);
        return streak;
    }

    public Optional<Streak> getStreakByUserId(int userId){
        return streakRepository.findByUserId(userId);
    }
}
