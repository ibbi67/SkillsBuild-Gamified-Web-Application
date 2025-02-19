package com.example.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LeaderboardEntry {
    @Id
    private int id;

    @OneToOne
    private User user;
    private int points;
}
