package com.hr.employeemanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr.employeemanagement.entity.LeaveRequest;
import com.hr.employeemanagement.enums.LeaveStatus;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    Page<LeaveRequest> findByEmployee_Id(Long employeeId, Pageable pageable);

    Page<LeaveRequest> findByStatus(LeaveStatus status, Pageable pageable);

    // JPQL - how many leaves of this employee overlap with the requested dates
    @Query("""
            SELECT COUNT(l)
            FROM LeaveRequest l
            WHERE l.employee.id = :employeeId
              AND l.status IN :statuses
              AND l.startDate <= :endDate
              AND l.endDate >= :startDate
            """)
    long countOverlappingLeaves(
            @Param("employeeId") Long employeeId,
            @Param("statuses") List<LeaveStatus> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
