package com.example.demo.config;

import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    private final UserService userService;

    @Autowired
    public SchedulerConfig(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void deleteUnverifiedUsersTask() {
        userService.deleteUnverifiedUsers();
    }

    // For testing purposes
    public void runDeleteUnverifiedUsersTask() {
        deleteUnverifiedUsersTask();
    }
}