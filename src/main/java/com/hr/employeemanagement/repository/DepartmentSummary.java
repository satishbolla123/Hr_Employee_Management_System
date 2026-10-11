package com.hr.employeemanagement.repository;

import java.math.BigDecimal;

public interface DepartmentSummary {

    String getDepartmentName();

    Long getActiveEmployees();

    BigDecimal getAverageSalary();
}
