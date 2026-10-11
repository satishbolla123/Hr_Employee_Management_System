package com.hr.employeemanagement.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.hr.employeemanagement.dto.request.AttendanceRequest;
import com.hr.employeemanagement.dto.response.AttendanceResponse;
import com.hr.employeemanagement.enums.AttendanceStatus;

public interface AttendanceService {

    // creates when id is null, updates otherwise
    ResponseEntity<AttendanceResponse> saveOrUpdateAttendance(Long id, AttendanceRequest request);

    ResponseEntity<AttendanceResponse> getAttendanceById(Long id);

    ResponseEntity<Page<AttendanceResponse>> searchAttendance(
            Long employeeId, LocalDate fromDate, LocalDate toDate, AttendanceStatus status,
            int page, int size, String sortBy, String direction);
}
