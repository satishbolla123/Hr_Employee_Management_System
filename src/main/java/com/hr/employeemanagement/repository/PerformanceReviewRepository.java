package com.hr.employeemanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr.employeemanagement.entity.PerformanceReview;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {

    boolean existsByEmployee_IdAndReviewPeriod(Long employeeId, String reviewPeriod);

    boolean existsByEmployee_IdAndReviewPeriodAndIdNot(Long employeeId, String reviewPeriod, Long id);

    Page<PerformanceReview> findByEmployee_Id(Long employeeId, Pageable pageable);

    // JPQL - average rating of one employee (null when there are no reviews yet)
    @Query("SELECT AVG(r.rating) FROM PerformanceReview r WHERE r.employee.id = :employeeId")
    Double findAverageRating(@Param("employeeId") Long employeeId);
}
