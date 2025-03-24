package com.example.backend.goals;

import com.example.backend.course.Course;
import com.example.backend.person.Person;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private LocalDate startDate; // Start date of the goal

    private LocalDate endDate; // End date of the goal

    private String description;
    private String reward;
    private boolean achieved;
    private int CourseID;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Goal(LocalDate startDate, LocalDate endDate, String Description,String reward, boolean achieved, int CourseID) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = Description;
        this.reward = reward;
        this.achieved = achieved;
        this.CourseID = CourseID;
    }
}
