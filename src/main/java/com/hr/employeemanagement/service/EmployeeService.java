package com.hr.employeemanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.hr.employeemanagement.dto.request.EmployeeRequest;
import com.hr.employeemanagement.dto.response.EmployeeResponse;

public interface EmployeeService {

    // creates when id is null, updates otherwise
    ResponseEntity<EmployeeResponse> saveOrUpdateEmployee(Long id, EmployeeRequest request);

    ResponseEntity<EmployeeResponse> getEmployeeById(Long id);

    ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            int page, int size, String sortBy, String direction);

    ResponseEntity<Page<EmployeeResponse>> searchEmployees(
            String keyword, int page, int size, String sortBy, String direction);

    ResponseEntity<Page<EmployeeResponse>> getEmployeesByDepartment(
            Long departmentId, int page, int size, String sortBy, String direction);

    ResponseEntity<EmployeeResponse> deactivateEmployee(Long id);
}
