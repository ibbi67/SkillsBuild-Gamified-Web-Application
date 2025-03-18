package com.example.backend.service;

import com.example.backend.domain.Streak;
import com.example.backend.domain.User;
import com.example.backend.repository.StreakRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void checkMaintainedStreak() {
        ///here we need to check if the user has logged in yesterday or if the streak has been broken
    }

    public Streak saveStreak(Streak streak) {
        streakRepository.save(streak);
        return streak;
    }

    public Optional<Streak> getStreakByUserId(int userId) {
        return streakRepository.findByUserId(userId);
    }

    public void checkAndUpdateStreak(User user) {
        Streak streak = user.getStreak();
        LocalDate today = LocalDate.now();
        LocalDate previousLoginDate = streak.getPreviousLogin() != null
            ? streak.getPreviousLogin().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
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
            saveStreak(streak);
        }
    }
}
