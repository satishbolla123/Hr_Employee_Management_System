package com.hr.employeemanagement.mapper;

import org.springframework.stereotype.Component;

import com.hr.employeemanagement.dto.request.ApplyLeaveRequest;
import com.hr.employeemanagement.dto.response.LeaveResponse;
import com.hr.employeemanagement.entity.Employee;
import com.hr.employeemanagement.entity.LeaveRequest;
import com.hr.employeemanagement.enums.LeaveStatus;

@Component
public class LeaveMapper {

    public LeaveRequest toEntity(ApplyLeaveRequest request, Employee employee, int totalDays) {

        LeaveRequest leave = new LeaveRequest();

        leave.setEmployee(employee);
        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setTotalDays(totalDays);
        leave.setReason(request.getReason().trim());
        leave.setStatus(LeaveStatus.PENDING);

        return leave;
    }

    public LeaveResponse toResponse(LeaveRequest leave) {

        LeaveResponse response = new LeaveResponse();

        response.setId(leave.getId());
        response.setEmployeeId(leave.getEmployee().getId());
        response.setEmployeeName(leave.getEmployee().getFirstName()
                + " " + leave.getEmployee().getLastName());
        response.setLeaveType(leave.getLeaveType());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setTotalDays(leave.getTotalDays());
        response.setReason(leave.getReason());
        response.setStatus(leave.getStatus());
        response.setReviewComment(leave.getReviewComment());
        response.setReviewedBy(leave.getReviewedBy());
        response.setReviewedOn(leave.getReviewedOn());
        response.setAppliedOn(leave.getCreatedDate());

        return response;
    }
}
