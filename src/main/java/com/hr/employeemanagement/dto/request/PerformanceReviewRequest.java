package com.hr.employeemanagement.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PerformanceReviewRequest {

    @NotNull(message = "Employee id is required")
    private Long employeeId;

    @NotBlank(message = "Review period is required")
    @Pattern(regexp = "^[0-9]{4}-(Q[1-4]|H[1-2])$", message = "Review period must look like 2026-Q3 or 2026-H1")
    private String reviewPeriod;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Integer rating;

    @Size(max = 500, message = "Strengths cannot exceed 500 characters")
    private String strengths;

    @Size(max = 500, message = "Improvement areas cannot exceed 500 characters")
    private String improvementAreas;

    @Size(max = 1000, message = "Comments cannot exceed 1000 characters")
    private String comments;

    @NotNull(message = "Review date is required")
    @PastOrPresent(message = "Review date cannot be in the future")
    private LocalDate reviewDate;

    public PerformanceReviewRequest() {
    }

    public Long getEmployeeId() {
        return employeeId;
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

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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
