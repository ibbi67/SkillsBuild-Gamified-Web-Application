package com.example.backend.repository;

import com.example.backend.domain.Statistics;
import com.example.backend.domain.Streak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StreakRepository extends JpaRepository<Streak, Integer> {
    public Streak findByUserId(Integer userId);
}
