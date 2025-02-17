package com.example.service;

import com.example.backend.domain.Course;
import com.example.backend.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DataLoader implements CommandLineRunner {

    private final CourseRepository courseRepository;

    public DataLoader(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if there are already courses in the database
        if (courseRepository.count() == 0) {
            // Add initial courses if none exist
            Course course1 = new Course("Course 1", "Description for Course 1", "https://example.com/course1");
            Course course2 = new Course("Course 2", "Description for Course 2", "https://example.com/course2");
            Course course3 = new Course("Course 3", "Description for Course 3", "https://example.com/course3");

            courseRepository.save(course1);
            courseRepository.save(course2);
            courseRepository.save(course3);

            System.out.println("Initial courses have been added to the database!");
        }
    }
}
