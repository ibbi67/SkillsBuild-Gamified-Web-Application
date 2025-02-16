package com.example.backend.controller;

import com.example.backend.repository.StreakRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.StreaksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/streak")
public class StreakController {
    @Autowired
    StreakRepository streakRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    StreaksService streaksService;

    @GetMapping
    public int getCurrentUserStreak(){
        ///need to find out how to retrieve the streak of the user that is currently logged in
        return 0;
    }

//    @GetMapping("/{userid}")
//    public int getCurrentStreakByUserId(@PathVariable Integer userId) {
//        return streaksService.findByUserId(userId).getStreak();
//    }

}
