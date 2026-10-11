package com.hr.employeemanagement.dto.response;

import java.time.LocalDate;

public class PerformanceReviewResponse {

    private Long id;

    private Long employeeId;

    private String employeeName;

    private String reviewPeriod;

    private Integer rating;

    private String strengths;

    private String improvementAreas;

    private String comments;

    private LocalDate reviewDate;

    public PerformanceReviewResponse() {
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getReviewPeriod() {
        return reviewPeriod;
    }

    public Integer getRating() {
        return rating;
    }

    public String getStrengths() {
        return strengths;
    }

    public String getImprovementAreas() {
        return improvementAreas;
    }

    public String getComments() {
        return comments;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setReviewPeriod(String reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public void setImprovementAreas(String improvementAreas) {
        this.improvementAreas = improvementAreas;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }
}
