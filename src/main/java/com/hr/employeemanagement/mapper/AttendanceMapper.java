package com.hr.employeemanagement.mapper;

import org.springframework.stereotype.Component;

import com.hr.employeemanagement.dto.request.AttendanceRequest;
import com.hr.employeemanagement.dto.response.AttendanceResponse;
import com.hr.employeemanagement.entity.Attendance;

@Component
public class AttendanceMapper {

    public void mapToEntity(AttendanceRequest request, Attendance attendance) {

        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setCheckInTime(request.getCheckInTime());
        attendance.setCheckOutTime(request.getCheckOutTime());
        attendance.setStatus(request.getStatus());
        attendance.setRemarks(request.getRemarks());
    }

    public AttendanceResponse toResponse(Attendance attendance) {

        AttendanceResponse response = new AttendanceResponse();

        response.setId(attendance.getId());
        response.setEmployeeId(attendance.getEmployee().getId());
        response.setEmployeeName(attendance.getEmployee().getFirstName()
                + " " + attendance.getEmployee().getLastName());
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setCheckInTime(attendance.getCheckInTime());
        response.setCheckOutTime(attendance.getCheckOutTime());
        response.setStatus(attendance.getStatus());
        response.setRemarks(attendance.getRemarks());

        return response;
    }
}
