package com.example.backend;

import com.example.backend.domain.Streak;
import com.example.backend.domain.User;
import com.example.backend.repository.StreakRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.StreaksService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BackendApplication implements CommandLineRunner {
    @Autowired
    UserRepository userRepository;
    @Autowired
    StreakRepository streakRepository;
    @Autowired
    StreaksService streaksService;

    @Autowired
    UserService userService;
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //dummy user 1
        User user = new User();
        user.setUsername("admin");
        user.setPassword("admin");
        user.getStreak().setUser(user);
        userService.save(user);

        System.out.println(user);

        //dummy user 2
        User user2 = new User();
        user2.setUsername("fren");
        user2.setPassword("fren");
        user2.getStreak().setUser(user2);
        userService.save(user2);
        System.out.println(user2);

        System.out.println(user.getStreak().getId());
        System.out.println(user2.getStreak().getId());


    }
}
