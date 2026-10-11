package com.hr.employeemanagement.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr.employeemanagement.dto.request.ApplyLeaveRequest;
import com.hr.employeemanagement.dto.request.LeaveDecisionRequest;
import com.hr.employeemanagement.dto.response.LeaveResponse;
import com.hr.employeemanagement.entity.Attendance;
import com.hr.employeemanagement.entity.Employee;
import com.hr.employeemanagement.entity.LeaveRequest;
import com.hr.employeemanagement.enums.AttendanceStatus;
import com.hr.employeemanagement.enums.EmployeeStatus;
import com.hr.employeemanagement.enums.LeaveStatus;
import com.hr.employeemanagement.exception.InvalidRequestException;
import com.hr.employeemanagement.exception.ResourceNotFoundException;
import com.hr.employeemanagement.mapper.LeaveMapper;
import com.hr.employeemanagement.repository.AttendanceRepository;
import com.hr.employeemanagement.repository.EmployeeRepository;
import com.hr.employeemanagement.repository.LeaveRepository;
import com.hr.employeemanagement.service.LeaveService;
import com.hr.employeemanagement.util.PageUtil;
import com.hr.employeemanagement.util.SecurityUtil;

@Service
public class LeaveServiceImpl implements LeaveService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);

    private static final List<String> SORT_FIELDS = List.of(
            "id", "startDate", "endDate", "status", "leaveType", "totalDays");

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveMapper leaveMapper;

    public LeaveServiceImpl(LeaveRepository leaveRepository,
            EmployeeRepository employeeRepository,
            AttendanceRepository attendanceRepository,
            LeaveMapper leaveMapper) {
        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveMapper = leaveMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<LeaveResponse> applyLeave(ApplyLeaveRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + request.getEmployeeId()));

        if (employee.getStatus() != EmployeeStatus.ACTIVE) {
            throw new InvalidRequestException("Inactive employees cannot apply for leave");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new InvalidRequestException("End date cannot be before start date");
        }

        int workingDays = countWorkingDays(request.getStartDate(), request.getEndDate());

        if (workingDays == 0) {
            throw new InvalidRequestException("Selected dates fall only on weekends");
        }

        long overlaps = leaveRepository.countOverlappingLeaves(
                employee.getId(),
                List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                request.getStartDate(),
                request.getEndDate());

        if (overlaps > 0) {
            throw new InvalidRequestException(
                    "A pending or approved leave already exists for these dates");
        }

        LeaveRequest saved = leaveRepository.save(
                leaveMapper.toEntity(request, employee, workingDays));

        logger.info("Leave {} applied by employee {} for {} day(s)",
                saved.getId(), employee.getEmployeeCode(), workingDays);

        return ResponseEntity.status(HttpStatus.CREATED).body(leaveMapper.toResponse(saved));
    }

    @Override
    @Transactional
    public ResponseEntity<LeaveResponse> approveLeave(Long id, LeaveDecisionRequest request) {
        return ResponseEntity.ok(reviewLeave(id, LeaveStatus.APPROVED, request));
    }

    @Override
    @Transactional
    public ResponseEntity<LeaveResponse> rejectLeave(Long id, LeaveDecisionRequest request) {
        return ResponseEntity.ok(reviewLeave(id, LeaveStatus.REJECTED, request));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<LeaveResponse> getLeaveById(Long id) {
        return ResponseEntity.ok(leaveMapper.toResponse(findLeaveOrThrow(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<LeaveResponse>> getAllLeaves(
            LeaveStatus status, int page, int size, String sortBy, String direction) {

        Pageable pageable = PageUtil.buildPageable(page, size, sortBy, direction, SORT_FIELDS);

        Page<LeaveRequest> leaves = (status == null)
                ? leaveRepository.findAll(pageable)
                : leaveRepository.findByStatus(status, pageable);

        return ResponseEntity.ok(leaves.map(leaveMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<LeaveResponse>> getLeavesByEmployee(
            Long employeeId, int page, int size, String sortBy, String direction) {

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        Pageable pageable = PageUtil.buildPageable(page, size, sortBy, direction, SORT_FIELDS);

        return ResponseEntity.ok(leaveRepository.findByEmployee_Id(employeeId, pageable)
                .map(leaveMapper::toResponse));
    }

    /**
     * Approve / reject share the same flow. Everything below runs in one transaction,
     * so if marking the attendance fails the leave stays PENDING.
     */
    private LeaveResponse reviewLeave(Long id, LeaveStatus decision, LeaveDecisionRequest request) {

        LeaveRequest leave = findLeaveOrThrow(id);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidRequestException("Leave request is already " + leave.getStatus());
        }

        leave.setStatus(decision);
        leave.setReviewComment(request == null ? null : request.getComment());
        leave.setReviewedBy(SecurityUtil.currentUsername());
        leave.setReviewedOn(LocalDateTime.now());

        if (decision == LeaveStatus.APPROVED) {
            markAttendanceAsOnLeave(leave);
        }

        LeaveRequest saved = leaveRepository.save(leave);

        logger.info("Leave {} {} by {}", id, decision, saved.getReviewedBy());

        return leaveMapper.toResponse(saved);
    }

    private void markAttendanceAsOnLeave(LeaveRequest leave) {

        Employee employee = leave.getEmployee();

        for (LocalDate date = leave.getStartDate(); !date.isAfter(leave.getEndDate()); date = date.plusDays(1)) {

            if (!isWorkingDay(date)) {
                continue;
            }

            Attendance attendance = attendanceRepository
                    .findByEmployee_IdAndAttendanceDate(employee.getId(), date)
                    .orElseGet(Attendance::new);

            attendance.setEmployee(employee);
            attendance.setAttendanceDate(date);
            attendance.setCheckInTime(null);
            attendance.setCheckOutTime(null);
            attendance.setStatus(AttendanceStatus.ON_LEAVE);
            attendance.setRemarks(leave.getLeaveType() + " leave approved");

            attendanceRepository.save(attendance);
        }
    }

    private int countWorkingDays(LocalDate start, LocalDate end) {

        int days = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (isWorkingDay(date)) {
                days++;
            }
        }

        return days;
    }

    private boolean isWorkingDay(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
    }

    private LeaveRequest findLeaveOrThrow(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave request not found with id: " + id));
    }
}
