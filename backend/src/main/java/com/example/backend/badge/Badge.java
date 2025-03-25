package com.example.backend.badge;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String criteriaType;

    @Column(nullable = false)
    private Integer criteriaValue;

    public Badge(String name, String description, String imageUrl, String criteriaType, Integer criteriaValue) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.criteriaType = criteriaType;
        this.criteriaValue = criteriaValue;
    }
}
