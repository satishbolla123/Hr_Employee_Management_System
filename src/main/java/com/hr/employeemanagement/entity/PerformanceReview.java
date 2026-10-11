package com.hr.employeemanagement.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "performance_reviews",
        uniqueConstraints = @UniqueConstraint(name = "uk_review_employee_period",
                columnNames = {"employee_id", "reviewPeriod"}))
public class PerformanceReview extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false, length = 10)
    private String reviewPeriod;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 500)
    private String strengths;

    @Column(length = 500)
    private String improvementAreas;

    @Column(length = 1000)
    private String comments;

    @Column(nullable = false)
    private LocalDate reviewDate;

    public PerformanceReview() {
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
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

    public void setEmployee(Employee employee) {
        this.employee = employee;
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
