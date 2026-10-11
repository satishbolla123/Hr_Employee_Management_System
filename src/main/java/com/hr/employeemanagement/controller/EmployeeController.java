package com.hr.employeemanagement.controller;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr.employeemanagement.dto.request.EmployeeRequest;
import com.hr.employeemanagement.dto.response.EmployeeResponse;
import com.hr.employeemanagement.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employees", description = "Employee management")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @Operation(summary = "Create an employee")
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {

        return employeeService.saveOrUpdateEmployee(null, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {

        return employeeService.saveOrUpdateEmployee(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by id")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {

        return employeeService.getEmployeeById(id);
    }

    @GetMapping
    @Operation(summary = "List employees with pagination and sorting")
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return employeeService.getAllEmployees(page, size, sortBy, direction);
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees by name or email")
    public ResponseEntity<Page<EmployeeResponse>> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return employeeService.searchEmployees(keyword, page, size, sortBy, direction);
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "List the employees of one department")
    public ResponseEntity<Page<EmployeeResponse>> getEmployeesByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return employeeService.getEmployeesByDepartment(departmentId, page, size, sortBy, direction);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate an employee (record is kept)")
    public ResponseEntity<EmployeeResponse> deactivateEmployee(@PathVariable Long id) {

        return employeeService.deactivateEmployee(id);
    }
}
