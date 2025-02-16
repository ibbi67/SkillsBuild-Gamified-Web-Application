package com.example.backend.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Streak {
    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    private User user;

    private int streak;
    private java.util.Date previousLogin; //this variable will store the date when streak is incremented

}
