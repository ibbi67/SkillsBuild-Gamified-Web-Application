package com.example.backend.repository;

import com.example.backend.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c WHERE c.difficulty BETWEEN :minDifficulty AND :maxDifficulty")
    List<Course> findCoursesByDifficultyRange(@Param("minDifficulty") int minDifficulty,
                                              @Param("maxDifficulty") int maxDifficulty);
}
