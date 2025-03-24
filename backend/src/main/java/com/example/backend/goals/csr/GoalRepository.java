package com.example.backend.goals.csr;

import com.example.backend.goals.Goal;
import com.example.backend.person.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByPerson(Person person);
    List<Goal> findByEndDateBefore(LocalDate date);
}