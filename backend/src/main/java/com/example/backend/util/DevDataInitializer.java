package com.example.backend.util;

import com.example.backend.badge.Badge;
import com.example.backend.badge.BadgeDTO;
import com.example.backend.badge.csr.BadgeRepository;
import com.example.backend.course.Course;
import com.example.backend.course.csr.CourseRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataInitializer {

    private final CourseRepository courseRepository;
    private final BadgeRepository badgeRepository;

    public DevDataInitializer(CourseRepository courseRepository, BadgeRepository badgeRepository) {
        this.courseRepository = courseRepository;
        this.badgeRepository = badgeRepository;
    }

    @PostConstruct
    public void init() {
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
                240,
                2));

        courseRepository.save(new Course("AI Foundations: A Collaboration of ISTE and IBM",
                """
                        There's no doubt about the importance of artificial intelligence to future generations! Your job will most certainly use some type of AI. In this digital learning offering, created by ISTE and IBM especially for high school students, you'll learn the foundational concepts behind AI systems, consider the ethical implications of AI, explore applications of AI tools, and more.

                        Complete five modules, including an AI Design Challenge, and pass the final assessment with a score of 80% or higher. You'll be rewarded with the AI Foundations credential.

                        Click Enroll to enroll in this learning plan and get started!

                        Language: English

                        Teacher Tip! Be sure to check out the Facilitator's Guide and AI Design Challenge Rubric, designed especially to support this course.
                        """,
                "https://students.yourlearning.ibm.com/activity/PLAN-B2125F145F0E",
                1170,
                3));
    }

    public void initBadgeData() {
        badgeRepository.save(new Badge("First Favorite", "Added your first course to favorites", "/badges/favorite-1.png", "FAVORITE", 1));
        badgeRepository.save(new Badge("Favorites Collector", "Added 2 courses to favorites","/badges/favorite-2.png", "FAVORITE", 2));
        badgeRepository.save(new Badge("Favorites Enthusiast", "Added 5 courses to favorites","/badges/favorite-5.png", "FAVORITE", 5));
        badgeRepository.save(new Badge("Favorites Addict", "Added 10 courses to favorites","/badges/favorite-10.png", "FAVORITE", 10));
        badgeRepository.save(new Badge("Streak Starter", "Maintained a 3-day streak",   "/badges/streak-3.png", "STREAK", 3));
        badgeRepository.save(new Badge("Streak Master", "Maintained a 7-day streak","/badges/streak-7.png", "STREAK", 7));
    }

}
