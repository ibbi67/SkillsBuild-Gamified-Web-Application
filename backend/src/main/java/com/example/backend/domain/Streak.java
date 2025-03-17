package com.example.backend.domain;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Streak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    @JsonBackReference
    private User user;

    private int streak;
    /**
     * this variable will store the date when streak is incremented
     */
    private java.util.Date previousLogin;
}
