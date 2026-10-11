package com.hr.employeemanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hr.employeemanagement.entity.Employee;
import com.hr.employeemanagement.enums.EmployeeStatus;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Page<Employee> findByDepartment_Id(Long departmentId, Pageable pageable);

    // JPQL - search on name or email
    @Query("""
            SELECT e
            FROM Employee e
            WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Employee> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // JPQL - used by the scheduler to find people who never checked in today
    @Query("""
            SELECT e
            FROM Employee e
            WHERE e.status = :status
              AND NOT EXISTS (
                    SELECT a.id
                    FROM Attendance a
                    WHERE a.employee = e
                      AND a.attendanceDate = :date)
            """)
    List<Employee> findEmployeesWithoutAttendance(
            @Param("status") EmployeeStatus status,
            @Param("date") LocalDate date);
}
