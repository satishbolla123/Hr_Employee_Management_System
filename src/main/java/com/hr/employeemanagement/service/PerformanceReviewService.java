package com.hr.employeemanagement.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.hr.employeemanagement.dto.request.PerformanceReviewRequest;
import com.hr.employeemanagement.dto.response.PerformanceReviewResponse;

public interface PerformanceReviewService {

    // creates when id is null, updates otherwise
    ResponseEntity<PerformanceReviewResponse> saveOrUpdateReview(Long id, PerformanceReviewRequest request);

    ResponseEntity<PerformanceReviewResponse> getReviewById(Long id);

    ResponseEntity<Page<PerformanceReviewResponse>> getReviewsByEmployee(
            Long employeeId, int page, int size, String sortBy, String direction);

    ResponseEntity<Map<String, Object>> getAverageRating(Long employeeId);
}
