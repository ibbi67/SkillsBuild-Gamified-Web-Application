package com.example.backend.enrollment.csr;

import com.example.backend.enrollment.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByPersonId(Long personId);
}
