package com.hr.employeemanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.hr.employeemanagement.dto.request.ApplyLeaveRequest;
import com.hr.employeemanagement.dto.request.LeaveDecisionRequest;
import com.hr.employeemanagement.dto.response.LeaveResponse;
import com.hr.employeemanagement.enums.LeaveStatus;

public interface LeaveService {

    ResponseEntity<LeaveResponse> applyLeave(ApplyLeaveRequest request);

    ResponseEntity<LeaveResponse> approveLeave(Long id, LeaveDecisionRequest request);

    ResponseEntity<LeaveResponse> rejectLeave(Long id, LeaveDecisionRequest request);

    ResponseEntity<LeaveResponse> getLeaveById(Long id);

    ResponseEntity<Page<LeaveResponse>> getAllLeaves(
            LeaveStatus status, int page, int size, String sortBy, String direction);

    ResponseEntity<Page<LeaveResponse>> getLeavesByEmployee(
            Long employeeId, int page, int size, String sortBy, String direction);
}
