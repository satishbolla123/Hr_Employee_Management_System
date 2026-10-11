package com.hr.employeemanagement.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr.employeemanagement.entity.Attendance;
import com.hr.employeemanagement.enums.AttendanceStatus;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByEmployee_IdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);

    Optional<Attendance> findByEmployee_IdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);

    // JPQL - employee and status are optional filters, the date range is always applied
    @Query("""
            SELECT a
            FROM Attendance a
            WHERE a.attendanceDate BETWEEN :fromDate AND :toDate
              AND (:employeeId IS NULL OR a.employee.id = :employeeId)
              AND (:status IS NULL OR a.status = :status)
            """)
    Page<Attendance> searchAttendance(
            @Param("employeeId") Long employeeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("status") AttendanceStatus status,
            Pageable pageable);
}
