package com.example.backend.controller;


import com.example.backend.domain.Statistics;
import com.example.backend.domain.User;
import com.example.backend.service.StatisticsService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    @GetMapping("/{userid}")
    public Statistics getStatistics(@PathVariable Integer userId) {

        return statisticsService.getStatisticsByUser(userId);
    }

    @PostMapping("/update")
    public Statistics updateStatistics(@RequestBody Statistics statistics) {
        return statisticsService.updateStatistics(statistics);
    }

}
