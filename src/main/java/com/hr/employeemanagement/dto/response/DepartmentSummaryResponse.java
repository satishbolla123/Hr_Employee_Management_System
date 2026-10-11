package com.hr.employeemanagement.dto.response;

import java.math.BigDecimal;

public class DepartmentSummaryResponse {

    private String departmentName;

    private Long activeEmployees;

    private BigDecimal averageSalary;

    public DepartmentSummaryResponse() {
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Long getActiveEmployees() {
        return activeEmployees;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setActiveEmployees(Long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public void setAverageSalary(BigDecimal averageSalary) {
        this.averageSalary = averageSalary;
    }
}
