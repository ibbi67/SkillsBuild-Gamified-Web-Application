package com.example.backend.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private User user;

    private int streak;
    private java.util.Date previousLogin; //this variable will store the date when streak is incremented

}
