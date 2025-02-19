package com.example.backend.service;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.LeaderboardEntry;
import com.example.backend.domain.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    private final UserRepository userRepository;

    @Autowired
    public LeaderboardService( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ApiResponse<List<LeaderboardEntry>> getLeaderboard() {
        List<User> users = userRepository.findAll();
        List<LeaderboardEntry> leaderboardEntries = users.stream()
                .map(this::createLeaderboardEntry)
                .sorted(Comparator.comparingInt(LeaderboardEntry::getPoints).reversed())
                .collect(Collectors.toList());

        return ApiResponse.success("Leaderboard fetched successfully", leaderboardEntries);
    }

    private LeaderboardEntry createLeaderboardEntry(User user) {
        int points = calculatePoints(user);
        return new LeaderboardEntry(user.getId(), user, points);
    }

    private int calculatePoints(User user) {
        int completedCourses = user.getEnrollments().size();
        double averageDifficulty = user.getEnrollments().stream()
                .mapToInt(enrollment -> enrollment.getCourse().getDifficulty())
                .average()
                .orElse(0);
        int streakDays = user.getStreak().getStreak();
        int enrollmentsCount = user.getEnrollments().size();

        return (int) ((completedCourses * averageDifficulty * 20) + (streakDays * 10) + (enrollmentsCount * 5));
    }
}