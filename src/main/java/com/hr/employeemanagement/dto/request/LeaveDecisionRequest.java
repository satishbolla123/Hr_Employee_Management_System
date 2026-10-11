package com.hr.employeemanagement.dto.request;

import jakarta.validation.constraints.Size;

public class LeaveDecisionRequest {

    @Size(max = 255, message = "Comment cannot exceed 255 characters")
    private String comment;

    public LeaveDecisionRequest() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
