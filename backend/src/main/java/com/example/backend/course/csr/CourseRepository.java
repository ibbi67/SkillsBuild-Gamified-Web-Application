package com.example.backend.course.csr;

import com.example.backend.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findTop10ByOrderByViewsDesc();
}
