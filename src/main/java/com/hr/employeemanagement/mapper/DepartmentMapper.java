package com.hr.employeemanagement.mapper;

import org.springframework.stereotype.Component;

import com.hr.employeemanagement.dto.request.DepartmentRequest;
import com.hr.employeemanagement.dto.response.DepartmentResponse;
import com.hr.employeemanagement.dto.response.DepartmentSummaryResponse;
import com.hr.employeemanagement.entity.Department;
import com.hr.employeemanagement.repository.DepartmentSummary;

@Component
public class DepartmentMapper {

    public void mapToEntity(DepartmentRequest request, Department department) {

        department.setName(request.getName().trim());
        department.setDescription(request.getDescription());
        department.setLocation(request.getLocation());
    }

    public DepartmentResponse toResponse(Department department) {

        DepartmentResponse response = new DepartmentResponse();

        response.setId(department.getId());
        response.setName(department.getName());
        response.setDescription(department.getDescription());
        response.setLocation(department.getLocation());
        response.setEmployeeCount(department.getEmployees().size());
        response.setCreatedDate(department.getCreatedDate());
        response.setLastModifiedDate(department.getLastModifiedDate());

        return response;
    }

    public DepartmentSummaryResponse toSummaryResponse(DepartmentSummary summary) {

        DepartmentSummaryResponse response = new DepartmentSummaryResponse();

        response.setDepartmentName(summary.getDepartmentName());
        response.setActiveEmployees(summary.getActiveEmployees());
        response.setAverageSalary(summary.getAverageSalary());

        return response;
    }
}
