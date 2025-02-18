package com.example.backend;

import com.example.backend.domain.Course;
import com.example.backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;


@SpringBootApplication
public class BackendApplication implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Override
    public void run(String... args) {
        initCourseData();
    }

    public void initCourseData() {
        courseRepository.save(new Course("Project Management Fundamentals",
                """
                        This course introduces you to the basic principles of project management and what it takes to be a successful project manager.
                        
                        The 3.5-hour course covers:
                        - Common project management terms
                        - The value of project management
                        - Project management approaches: Waterfall, Agile, and Hybrid
                        - The role, overall responsibilities, and competencies of a project manager
                        - The purpose of the phases of a project: Initiate and Plan, Execute, and Close
                        - The key tasks that a project manager performs in each project phase
                        - The overall job market and common industry certifications to consider in the project management field
                        
                        You also get to put on your project manager “hat” to respond to some interactive situations based on a project scenario.
                        \s
                        Opportunity to earn a credential: Complete this course to earn the Project Management Fundamentals credential, a co-creation of IBM and IPMA!
                        
                        Click Start tracking progress to enroll in this learning plan and get started!""",
                "https://students.yourlearning.ibm.com/activity/PLAN-14A47D1900AA",
                Duration.ofHours(4),
                "beginner"));
    }
}
