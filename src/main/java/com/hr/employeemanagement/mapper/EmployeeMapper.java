package com.hr.employeemanagement.mapper;

import java.util.Locale;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hr.employeemanagement.dto.request.EmployeeRequest;
import com.hr.employeemanagement.dto.response.EmployeeResponse;
import com.hr.employeemanagement.entity.Employee;
import com.hr.employeemanagement.entity.EmployeeProfile;
import com.hr.employeemanagement.entity.Skill;

@Component
public class EmployeeMapper {

    /**
     * Copies the simple fields of the request (and the profile details) into the entity.
     * Department and skills are resolved by the service because they need the database.
     */
    public void mapToEntity(EmployeeRequest request, Employee employee) {

        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName().trim());
        employee.setLastName(request.getLastName().trim());
        employee.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setHireDate(request.getHireDate());
        employee.setDesignation(request.getDesignation().trim());
        employee.setSalary(request.getSalary());

        EmployeeProfile profile = employee.getProfile();

        if (profile == null) {
            profile = new EmployeeProfile();
            employee.setProfile(profile);
        }

        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setAddress(request.getAddress());
        profile.setCity(request.getCity());
        profile.setEmergencyContactName(request.getEmergencyContactName());
        profile.setEmergencyContactNumber(request.getEmergencyContactNumber());
    }

    public EmployeeResponse toResponse(Employee employee) {

        EmployeeResponse response = new EmployeeResponse();

        response.setId(employee.getId());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setPhoneNumber(employee.getPhoneNumber());
        response.setHireDate(employee.getHireDate());
        response.setDesignation(employee.getDesignation());
        response.setSalary(employee.getSalary());
        response.setStatus(employee.getStatus());
        response.setDepartmentId(employee.getDepartment().getId());
        response.setDepartmentName(employee.getDepartment().getName());

        EmployeeProfile profile = employee.getProfile();

        if (profile != null) {
            response.setDateOfBirth(profile.getDateOfBirth());
            response.setAddress(profile.getAddress());
            response.setCity(profile.getCity());
            response.setEmergencyContactName(profile.getEmergencyContactName());
            response.setEmergencyContactNumber(profile.getEmergencyContactNumber());
        }

        response.setSkills(employee.getSkills()
                .stream()
                .map(Skill::getName)
                .collect(Collectors.toCollection(TreeSet::new)));

        response.setCreatedDate(employee.getCreatedDate());
        response.setLastModifiedDate(employee.getLastModifiedDate());

        return response;
    }
}
