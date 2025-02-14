package com.example.backend.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Statistics {

    @Id
    @GeneratedValue
    private int id;
    @OneToOne
    @JoinColumn(nullable = false)
    private User user;

    private int streak;
    private double xp;
    private int level;
    private int coursesCompleted;
    @Temporal(TemporalType.DATE)
    private java.util.Date lastLogin;


}
