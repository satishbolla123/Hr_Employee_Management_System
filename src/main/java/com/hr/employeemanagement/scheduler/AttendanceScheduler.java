package com.hr.employeemanagement.scheduler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hr.employeemanagement.entity.Attendance;
import com.hr.employeemanagement.entity.Employee;
import com.hr.employeemanagement.enums.AttendanceStatus;
import com.hr.employeemanagement.enums.EmployeeStatus;
import com.hr.employeemanagement.repository.AttendanceRepository;
import com.hr.employeemanagement.repository.EmployeeRepository;

@Component
public class AttendanceScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceScheduler.class);

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public AttendanceScheduler(EmployeeRepository employeeRepository,
            AttendanceRepository attendanceRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    /**
     * Runs every weekday evening (cron is in application.properties) and marks ABSENT
     * for active employees who have no attendance record for the day.
     */
    @Scheduled(cron = "${app.attendance.auto-absent-cron}")
    @Transactional
    public void markAbsentees() {

        LocalDate today = LocalDate.now();

        List<Employee> missing = employeeRepository
                .findEmployeesWithoutAttendance(EmployeeStatus.ACTIVE, today);

        if (missing.isEmpty()) {
            logger.info("Auto absent job: everyone has attendance for {}", today);
            return;
        }

        List<Attendance> absentees = new ArrayList<>();

        for (Employee employee : missing) {
            Attendance attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setAttendanceDate(today);
            attendance.setStatus(AttendanceStatus.ABSENT);
            attendance.setRemarks("Auto marked absent by the system");
            absentees.add(attendance);
        }

        attendanceRepository.saveAll(absentees);

        logger.info("Auto absent job: marked {} employee(s) absent for {}", absentees.size(), today);
    }
}
