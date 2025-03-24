package com.example.backend.badge;

import com.example.backend.badge.csr.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BadgeInitializer implements CommandLineRunner {

    private final BadgeService badgeService;

    @Autowired
    public BadgeInitializer(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @Override
    public void run(String... args) {
        System.out.println("Initializing badges...");
        badgeService.initializeDefaultBadges();
    }
} 