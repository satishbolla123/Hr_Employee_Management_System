package com.hr.employeemanagement.controller;

import java.util.Map;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hr.employeemanagement.dto.request.PerformanceReviewRequest;
import com.hr.employeemanagement.dto.response.PerformanceReviewResponse;
import com.hr.employeemanagement.service.PerformanceReviewService;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Performance Reviews", description = "Employee performance reviews")
public class PerformanceReviewController {

    private final PerformanceReviewService reviewService;

    public PerformanceReviewController(PerformanceReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Add a performance review")
    public ResponseEntity<PerformanceReviewResponse> createReview(
            @Valid @RequestBody PerformanceReviewRequest request) {

        return reviewService.saveOrUpdateReview(null, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a performance review")
    public ResponseEntity<PerformanceReviewResponse> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody PerformanceReviewRequest request) {

        return reviewService.saveOrUpdateReview(id, request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a review by id")
    public ResponseEntity<PerformanceReviewResponse> getReviewById(@PathVariable Long id) {

        return reviewService.getReviewById(id);
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "All reviews of one employee")
    public ResponseEntity<Page<PerformanceReviewResponse>> getReviewsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reviewDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        return reviewService.getReviewsByEmployee(employeeId, page, size, sortBy, direction);
    }

    @GetMapping("/employee/{employeeId}/average")
    @Operation(summary = "Average rating of one employee")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable Long employeeId) {

        return reviewService.getAverageRating(employeeId);
    }
}
