package com.example.backend.service;

import com.example.backend.domain.User;
import com.example.backend.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.backend.domain.Statistics;

@Service
public class StatisticsService {
    @Autowired
    StatisticsRepository statisticsRepository;
    @Autowired
    public StatisticsService(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    public Statistics getStatisticsByUser(Integer userId) {
        return statisticsRepository.findByUserId(userId);
    }

    public Statistics updateStatistics(Statistics statistics){
        return statisticsRepository.save(statistics);
    }

    public Statistics incrementStreak(Statistics statistics){
        System.out.println("testing");

        return statisticsRepository.save(statistics);
    }

}
