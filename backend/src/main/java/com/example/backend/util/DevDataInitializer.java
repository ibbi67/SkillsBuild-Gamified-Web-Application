package com.example.backend.util;

import com.example.backend.badge.Badge;
import com.example.backend.badge.csr.BadgeRepository;
import com.example.backend.course.Course;
import com.example.backend.course.csr.CourseRepository;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataInitializer {

    private final CourseRepository courseRepository;
    private final BadgeRepository badgeRepository;
    private final PersonRepository personRepository;

    public DevDataInitializer(CourseRepository courseRepository, BadgeRepository badgeRepository, PersonRepository personRepository) {
        this.courseRepository = courseRepository;
        this.badgeRepository = badgeRepository;
        this.personRepository = personRepository;
    }

    @PostConstruct
    public void init() {
        initCourseData();
        initBadgeData();
        initPersonData();
//        initFriendRelationships();
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

        courseRepository.save(new Course("Cloud Computing Fundamentals",
                """
                        This credential earner demonstrates knowledge of cloud computing, including cloud services, deployment models,
                        virtualization, orchestration, and cloud security. The individual is aware of cloud benefits for users and businesses.
                        The individual has a conceptual understanding of how to create a container, deploy a web app to the cloud, and
                        analyze security in a simulated environment. The earner is aware of the job outlook in cloud computing and the
                        skills required for success in various roles.""",
                "https://students.yourlearning.ibm.com/activity/PLAN-58FA14F64C9B",
                600,
                3));

        courseRepository.save(new Course("Cybersecurity Fundamentals",
                """
                        This course will provide you with an introduction to cybersecurity.
                        From the offense perspective, you will learn about cyberattackers,
                        their tactics, social engineering, and high-profile case studies. From
                        the defense perspective, you will learn about common approaches
                        organizations take to prevent, detect, and respond to cyberattacks.
                        You will also learn about career opportunities in this exciting, growing
                        field. Finally, you’ll explore how new developments and emerging
                        technologies, such as artificial intelligence (AI), impact cybersecurity.""",
                "https://students.yourlearning.ibm.com/activity/PLAN-805005E992EA",
                420,
                2));

        courseRepository.save(new Course("Data Fundamentals",
                """
                Do you love to discover meaning in facts and numbers? Learn the
                concepts and methods of data science and how its discoveries change the world.
                Then get hands-on practice cleaning, refining, and visualizing data, in a series
                of simulations, using IBM Watson Studio with the data refinery tool. Finish by
                gathering tips and resources that can help you launch a great career in data science.

                Complete the following required courses to earn an industry-recognized IBM SkillsBuild digital credential called Data Fundamentals:
                """,
                "https://students.yourlearning.ibm.com/activity/PLAN-0EC2BCEA3C39",
                420,
                4));

        courseRepository.save(new Course("Web Development Fundamentals",
                """
                    Would you like opportunities to express yourself creatively on the internet?
                    Web development is an exciting, growing field in tech. Learn the basics about the languages, tools,
                    and processes to develop websites. Then, get hands-on practice creating an interactive task list web
                    page in a series of simulations. Finish by gathering tips and resources that can help you launch a
                    great career in web development.
                    
                    Complete the following required courses to earn an industry-recognized IBM SkillsBuild
                    digital credential called Web Development Fundamentals:
                    """,
                "https://students.yourlearning.ibm.com/activity/PLAN-43A030B97485",
                720,
                4));

        courseRepository.save(new Course("Explore Emerging Tech",
                """
                        Curious about tech, but not sure where to focus? You've come to the right place.
                        
                        In this learning plan, you'll get an intro to six emerging technologies that
                        power today's jobs. You'll learn a little about each—like foundational concepts,
                        terminology, and how it's used—and then you can decide where you want to go deeper.
                        
                        Complete all the courses in this learning plan and earn the Explore Emerging Tech credential.
                        """,
                "https://students.yourlearning.ibm.com/activity/PLAN-91F302DE9BBD",
                1170,
                2));

        courseRepository.save(new Course("Open Source Origin Stories",
                """
                   Interested in a career in technology but not sure where to begin?
                   Explore ways that hybrid cloud computing, artificial intelligence,
                   and open source code reach across many tech opportunities ranging
                   from high-powered data management to ethical AI. In three interactive
                   superhero origin stories, you’ll learn how teams work together in
                   open source tech and use it to transform our world.
                   """,
                "https://students.yourlearning.ibm.com/activity/PLAN-DEAEB6C8F149",
                300,
                1));

        courseRepository.save(new Course("Customer Engagement Fundamentals",
                """
                        If you’re intrigued by the field of customer engagement,
                        this course is for you. Learn the skills to build relationships
                        with customers, help them solve stressful problems, and win their
                        loyalty. In this course, you’ll discover strategies to build
                        rapport with customers, develop communication skills to troubleshoot
                        issues and guide customers through solutions, use scientific methods
                        to solve problems creatively, and become a productive team member.
                        
                        Complete the following required modules to earn an industry-recognized
                        IBM SkillsBuild digital credential called Customer Engagement Fundamentals:""",
                "https://students.yourlearning.ibm.com/activity/PLAN-F77885B0DEE9",
                900,
                1));

        courseRepository.save(new Course("Quantum Enigmas",
                """
                        Embark on a journey through the key concepts of quantum computing
                        and fascinating world of quantum enigmas. These courses are designed
                        to provide you with a deeper understanding of quantum computing
                        principles and techniques through the exploration and solving of
                        intriguing quantum enigmas.\s
                        
                        These courses were developed in collaboration with Institut quantique de
                        l’Université de Sherbrooke.
                        
                        What you’ll learn
                        
                        After completing Quantum Enigmas, you should be able to:
                        
                        - Differentiate between classical and quantum computers by
                        identifying their fundamental differences, including computational
                        principles and technologies
                        - Describe the measurement process of a qubit in quantum computing
                        and the outcomes and implications of measuring a qubit
                        - Identify the roles and significance of quantum logic gates
                        in quantum computing
                        - Describe how quantum logic gates contribute to quantum
                        information processing
                        - Differentiate between various types of quantum gates,
                        their unique functionalities, and application in quantum computing systems
                        - Describe how to use the IBM quantum composer to
                        manipulate qubits, demonstrating the process of placing a
                        qubit into superposition, entangling qubits, and applying different
                        types of quantum gates to control and manipulate quantum states effectively""",
                "https://students.yourlearning.ibm.com/activity/PLAN-63384B7F59EC",
                480,
                4));

        courseRepository.save(new Course("User Experience Design Fundamentals",
                """
                        Are you passionate about creating exceptional user experiences?
                        UX design is a dynamic and in-demand field that focuses on crafting
                        digital products that are intuitive, user-friendly, and visually
                        appealing. Learn about design concepts and the UX design process,
                        from conducting research to creating wireframes and prototypes to
                        conducting usability tests and implementing feedback. Through an
                        example UX design case study, apply your learning of UX design
                        principles and practices. Finish by gathering tips and resources
                        that can help you launch a great career in UX Design.\s
                        
                        Complete the following required courses to earn an industry-recognized
                        IBM SkillsBuild digital credential called User Experience Design Fundamentals:\s""",
                "https://students.yourlearning.ibm.com/activity/PLAN-44E67AF54225",
                720,
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

    public void initPersonData() {
        personRepository.save(new Person(
                "sarah_dev",
                "$2a$12$8qx5ZWZoNW8iAH1q4CTJIe.rLkUj3y9vVnXK9VCHqCS4eYRFgfLtG",
                8,
                "Sarah",
                "Chen",
                "sarah.chen@email.com",
                "https://randomuser.me/api/portraits/women/1.jpg"
        ));

        personRepository.save(new Person(
                "alex_coder",
                "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKXcQQhJD0n/14q",
                6,
                "Alex",
                "Rodriguez",
                "alex.rodriguez@email.com",
                "https://randomuser.me/api/portraits/men/2.jpg"
        ));

        personRepository.save(new Person(
                "emma_tech",
                "$2a$12$QmKxM7xqzxMBGpVtFBp2SO5Pf/F6FAqUw8GaO3PGYVKcNAeA7wzYe",
                9,
                "Emma",
                "Wilson",
                "emma.wilson@email.com",
                "https://randomuser.me/api/portraits/women/3.jpg"
        ));

        personRepository.save(new Person(
                "marcus_js",
                "$2a$12$rTtPfMRF9DDgQCOrYT3EyebF0X0JW3XMCDj6J3w8TgJnQvK0NH2da",
                2,
                "Marcus",
                "Johnson",
                "marcus.johnson@email.com",
                "https://randomuser.me/api/portraits/men/4.jpg"
        ));

        personRepository.save(new Person(
                "priya_code",
                "$2a$12$9ZxF1g/e.0kRfZP6zvQXR.TUHBGHDXVZEvrCwgZ8r.U8D9zGqF3TG",
                3,
                "Priya",
                "Patel",
                "priya.patel@email.com",
                "https://randomuser.me/api/portraits/women/5.jpg"
        ));

        personRepository.save(new Person(
                "david_py",
                "$2a$12$tX4S7wM3CK7jC2oqW1BTE.x3pJ9F0h7uQyq0tK9Jh8NIZKjJ0r1.q",
                12,
                "David",
                "Smith",
                "david.smith@email.com",
                "https://randomuser.me/api/portraits/men/6.jpg"
        ));

        personRepository.save(new Person(
                "lisa_ux",
                "$2a$12$KG6H8J2u8qHRqm.6i2rIKuwkUhC.v8QJh/wC.kHWNXNqpkD2NZlt2",
                6,
                "Lisa",
                "Brown",
                "lisa.brown@email.com",
                "https://randomuser.me/api/portraits/women/7.jpg"
        ));

        personRepository.save(new Person(
                "james_ai",
                "$2a$12$1VqdrKP7YqS.ZRH90oI9zOIUFtqX9vhRzZ6oKx.0jJ0fvK1l3eJlq",
                9,
                "James",
                "Williams",
                "james.williams@email.com",
                "https://randomuser.me/api/portraits/men/8.jpg"
        ));

        personRepository.save(new Person(
                "sofia_data",
                "$2a$12$mKZ8P0ZyQ3X9Y8YqR.zVv.3z8x9.U8q9tfbQZ9K6kH0TvKJ.4GXEC",
                2,
                "Sofia",
                "Garcia",
                "sofia.garcia@email.com",
                "https://randomuser.me/api/portraits/women/9.jpg"
        ));

        personRepository.save(new Person(
                "ryan_dev",
                "$2a$12$QK0JE/KlM0e.Z2VKvEeC8e4WgHpL7ObJ0QoRywU.JYFoX8hKX2p.2",
                5,
                "Ryan",
                "Taylor",
                "ryan.taylor@email.com",
                "https://randomuser.me/api/portraits/men/10.jpg"
        ));
    }
    
    public void initFriendRelationships() {
        // Get existing people from the repository
        Person sarah = personRepository.findByUsername("sarah_dev").orElse(null);
        Person alex = personRepository.findByUsername("alex_coder").orElse(null);
        Person emma = personRepository.findByUsername("emma_tech").orElse(null);
        Person marcus = personRepository.findByUsername("marcus_js").orElse(null);
        Person priya = personRepository.findByUsername("priya_code").orElse(null);
        
        if (sarah != null && alex != null && emma != null && marcus != null && priya != null) {
            // Add friend relationships
            // Sarah is friends with Alex and Emma
            sarah.getFriends().add(alex);
            sarah.getFriends().add(emma);
            
            // Alex is friends with Emma and Marcus
            alex.getFriends().add(emma);
            alex.getFriends().add(marcus);
            
            // Emma is friends with Priya
            emma.getFriends().add(priya);
            
            // Save the updated person entities
            personRepository.save(sarah);
            personRepository.save(alex);
            personRepository.save(emma);
            personRepository.save(marcus);
            personRepository.save(priya);
        }
    }

}
