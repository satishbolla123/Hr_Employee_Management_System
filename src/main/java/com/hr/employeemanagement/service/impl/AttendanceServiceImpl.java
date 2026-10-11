package com.hr.employeemanagement.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr.employeemanagement.dto.request.AttendanceRequest;
import com.hr.employeemanagement.dto.response.AttendanceResponse;
import com.hr.employeemanagement.entity.Attendance;
import com.hr.employeemanagement.entity.Employee;
import com.hr.employeemanagement.enums.AttendanceStatus;
import com.hr.employeemanagement.enums.EmployeeStatus;
import com.hr.employeemanagement.exception.DuplicateResourceException;
import com.hr.employeemanagement.exception.InvalidRequestException;
import com.hr.employeemanagement.exception.ResourceNotFoundException;
import com.hr.employeemanagement.mapper.AttendanceMapper;
import com.hr.employeemanagement.repository.AttendanceRepository;
import com.hr.employeemanagement.repository.EmployeeRepository;
import com.hr.employeemanagement.service.AttendanceService;
import com.hr.employeemanagement.util.PageUtil;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    private static final List<String> SORT_FIELDS = List.of(
            "id", "attendanceDate", "status", "checkInTime", "checkOutTime");

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceMapper attendanceMapper;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
            EmployeeRepository employeeRepository,
            AttendanceMapper attendanceMapper) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceMapper = attendanceMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<AttendanceResponse> saveOrUpdateAttendance(
            Long id, AttendanceRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + request.getEmployeeId()));

        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new InvalidRequestException("Attendance cannot be recorded for an inactive employee");
        }

        validateTimings(request);

        boolean isNew = (id == null);
        Attendance attendance;

        if (isNew) {
            if (attendanceRepository.existsByEmployee_IdAndAttendanceDate(
                    employee.getId(), request.getAttendanceDate())) {
                throw new DuplicateResourceException("Attendance is already marked for "
                        + employee.getEmployeeCode() + " on " + request.getAttendanceDate());
            }
            attendance = new Attendance();
        } else {
            attendance = attendanceRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Attendance record not found with id: " + id));

            Optional<Attendance> sameDay = attendanceRepository
                    .findByEmployee_IdAndAttendanceDate(employee.getId(), request.getAttendanceDate());

            if (sameDay.isPresent() && !sameDay.get().getId().equals(id)) {
                throw new DuplicateResourceException("Another attendance record already exists for "
                        + employee.getEmployeeCode() + " on " + request.getAttendanceDate());
            }
        }

        attendanceMapper.mapToEntity(request, attendance);
        attendance.setEmployee(employee);

        Attendance saved = attendanceRepository.save(attendance);

        logger.info("Attendance {} for employee {} on {}", isNew ? "marked" : "updated",
                employee.getEmployeeCode(), saved.getAttendanceDate());

        return ResponseEntity
                .status(isNew ? HttpStatus.CREATED : HttpStatus.OK)
                .body(attendanceMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<AttendanceResponse> getAttendanceById(Long id) {

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance record not found with id: " + id));

        return ResponseEntity.ok(attendanceMapper.toResponse(attendance));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<AttendanceResponse>> searchAttendance(
            Long employeeId, LocalDate fromDate, LocalDate toDate, AttendanceStatus status,
            int page, int size, String sortBy, String direction) {

        // default window is the month of fromDate (or the current month)
        LocalDate from = (fromDate != null) ? fromDate : LocalDate.now().withDayOfMonth(1);
        LocalDate to = (toDate != null) ? toDate : from.withDayOfMonth(from.lengthOfMonth());

        if (to.isBefore(from)) {
            throw new InvalidRequestException("toDate cannot be before fromDate");
        }

        if (employeeId != null && !employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        Pageable pageable = PageUtil.buildPageable(page, size, sortBy, direction, SORT_FIELDS);

        Page<AttendanceResponse> result = attendanceRepository
                .searchAttendance(employeeId, from, to, status, pageable)
                .map(attendanceMapper::toResponse);

        return ResponseEntity.ok(result);
    }

    private void validateTimings(AttendanceRequest request) {

        boolean needsCheckIn = request.getStatus() == AttendanceStatus.PRESENT
                || request.getStatus() == AttendanceStatus.HALF_DAY;

        if (needsCheckIn && request.getCheckInTime() == null) {
            throw new InvalidRequestException("Check-in time is required for PRESENT or HALF_DAY");
        }

        if (request.getCheckInTime() != null && request.getCheckOutTime() != null
                && !request.getCheckOutTime().isAfter(request.getCheckInTime())) {
            throw new InvalidRequestException("Check-out time must be after check-in time");
        }
    }
}
