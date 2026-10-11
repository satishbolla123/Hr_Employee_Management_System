package com.hr.employeemanagement.controller;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr.employeemanagement.dto.request.ApplyLeaveRequest;
import com.hr.employeemanagement.dto.request.LeaveDecisionRequest;
import com.hr.employeemanagement.dto.response.LeaveResponse;
import com.hr.employeemanagement.enums.LeaveStatus;
import com.hr.employeemanagement.service.LeaveService;

@RestController
@RequestMapping("/api/leaves")
@Tag(name = "Leaves", description = "Apply, approve and reject leave")
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping
    @Operation(summary = "Apply for leave")
    public ResponseEntity<LeaveResponse> applyLeave(@Valid @RequestBody ApplyLeaveRequest request) {

        return leaveService.applyLeave(request);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Approve a pending leave (admin only)")
    public ResponseEntity<LeaveResponse> approveLeave(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) LeaveDecisionRequest request) {

        return leaveService.approveLeave(id, request);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Reject a pending leave (admin only)")
    public ResponseEntity<LeaveResponse> rejectLeave(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) LeaveDecisionRequest request) {

        return leaveService.rejectLeave(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a leave request by id")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable Long id) {

        return leaveService.getLeaveById(id);
    }

    @GetMapping
    @Operation(summary = "List leave requests, optionally filtered by status")
    public ResponseEntity<Page<LeaveResponse>> getAllLeaves(
            @RequestParam(required = false) LeaveStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return leaveService.getAllLeaves(status, page, size, sortBy, direction);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Leave history of one employee")
    public ResponseEntity<Page<LeaveResponse>> getLeavesByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return leaveService.getLeavesByEmployee(employeeId, page, size, sortBy, direction);
    }
}
