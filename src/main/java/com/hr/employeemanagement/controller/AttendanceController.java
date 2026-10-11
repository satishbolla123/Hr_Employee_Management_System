package com.hr.employeemanagement.controller;

import java.time.LocalDate;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr.employeemanagement.dto.request.AttendanceRequest;
import com.hr.employeemanagement.dto.response.AttendanceResponse;
import com.hr.employeemanagement.enums.AttendanceStatus;
import com.hr.employeemanagement.service.AttendanceService;

@RestController
@RequestMapping("/api/attendance")
@Tag(name = "Attendance", description = "Mark and search attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    @Operation(summary = "Mark attendance for an employee")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @Valid @RequestBody AttendanceRequest request) {

        return attendanceService.saveOrUpdateAttendance(null, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Correct an attendance record")
    public ResponseEntity<AttendanceResponse> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request) {

        return attendanceService.saveOrUpdateAttendance(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one attendance record")
    public ResponseEntity<AttendanceResponse> getAttendanceById(@PathVariable Long id) {

        return attendanceService.getAttendanceById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search attendance by employee, date range and status")
    public ResponseEntity<Page<AttendanceResponse>> searchAttendance(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) AttendanceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "attendanceDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return attendanceService.searchAttendance(
                employeeId, fromDate, toDate, status, page, size, sortBy, direction);
    }
}
