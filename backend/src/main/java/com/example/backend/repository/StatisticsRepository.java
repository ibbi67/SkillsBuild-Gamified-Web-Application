package com.example.backend.repository;

import com.example.backend.domain.Statistics;
import com.example.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    public Statistics findByUserId(Integer userId);


}
