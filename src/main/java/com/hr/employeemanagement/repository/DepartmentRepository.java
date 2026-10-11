package com.hr.employeemanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hr.employeemanagement.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    // native query - headcount and average salary of active employees per department
    @Query(value = """
            SELECT d.name AS departmentName,
                   COUNT(e.id) AS activeEmployees,
                   COALESCE(AVG(e.salary), 0) AS averageSalary
            FROM departments d
            LEFT JOIN employees e
                   ON e.department_id = d.id AND e.status = 'ACTIVE'
            GROUP BY d.id, d.name
            ORDER BY d.name
            """, nativeQuery = true)
    List<DepartmentSummary> getDepartmentSummary();
}
