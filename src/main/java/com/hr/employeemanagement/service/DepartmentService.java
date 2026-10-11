package com.hr.employeemanagement.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.hr.employeemanagement.dto.request.DepartmentRequest;
import com.hr.employeemanagement.dto.response.DepartmentResponse;
import com.hr.employeemanagement.dto.response.DepartmentSummaryResponse;

public interface DepartmentService {

    // creates when id is null, updates otherwise
    ResponseEntity<DepartmentResponse> saveOrUpdateDepartment(Long id, DepartmentRequest request);

    ResponseEntity<DepartmentResponse> getDepartmentById(Long id);

    ResponseEntity<Page<DepartmentResponse>> getAllDepartments(
            int page, int size, String sortBy, String direction);

    ResponseEntity<List<DepartmentSummaryResponse>> getDepartmentSummary();

    ResponseEntity<String> deleteDepartment(Long id);
}
