package com.hr.employeemanagement.controller;

import java.util.List;

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

import com.hr.employeemanagement.dto.request.DepartmentRequest;
import com.hr.employeemanagement.dto.response.DepartmentResponse;
import com.hr.employeemanagement.dto.response.DepartmentSummaryResponse;
import com.hr.employeemanagement.service.DepartmentService;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Departments", description = "Create, update, view and delete departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    @Operation(summary = "Create a department")
    public ResponseEntity<DepartmentResponse> createDepartment(
            @Valid @RequestBody DepartmentRequest request) {

        return departmentService.saveOrUpdateDepartment(null, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {

        return departmentService.saveOrUpdateDepartment(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a department by id")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {

        return departmentService.getDepartmentById(id);
    }

    @GetMapping
    @Operation(summary = "List departments with pagination and sorting")
    public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        return departmentService.getAllDepartments(page, size, sortBy, direction);
    }

    @GetMapping("/summary")
    @Operation(summary = "Active headcount and average salary per department")
    public ResponseEntity<List<DepartmentSummaryResponse>> getDepartmentSummary() {

        return departmentService.getDepartmentSummary();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an empty department")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {

        return departmentService.deleteDepartment(id);
    }
}
