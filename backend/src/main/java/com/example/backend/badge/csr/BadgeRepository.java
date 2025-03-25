package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Integer> {

    Optional<Badge> findByName(String name);
    List<Badge> findByCriteriaType(String criteriaType);
    Optional<Badge> findByCriteriaTypeAndCriteriaValue(String criteriaType, Integer criteriaValue);
} 
