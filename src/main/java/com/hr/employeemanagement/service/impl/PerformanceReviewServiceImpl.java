package com.hr.employeemanagement.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr.employeemanagement.dto.request.PerformanceReviewRequest;
import com.hr.employeemanagement.dto.response.PerformanceReviewResponse;
import com.hr.employeemanagement.entity.Employee;
import com.hr.employeemanagement.entity.PerformanceReview;
import com.hr.employeemanagement.exception.DuplicateResourceException;
import com.hr.employeemanagement.exception.ResourceNotFoundException;
import com.hr.employeemanagement.mapper.PerformanceReviewMapper;
import com.hr.employeemanagement.repository.EmployeeRepository;
import com.hr.employeemanagement.repository.PerformanceReviewRepository;
import com.hr.employeemanagement.service.PerformanceReviewService;
import com.hr.employeemanagement.util.PageUtil;

@Service
public class PerformanceReviewServiceImpl implements PerformanceReviewService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceReviewServiceImpl.class);

    private static final List<String> SORT_FIELDS = List.of("id", "reviewDate", "rating", "reviewPeriod");

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;
    private final PerformanceReviewMapper reviewMapper;

    public PerformanceReviewServiceImpl(PerformanceReviewRepository reviewRepository,
            EmployeeRepository employeeRepository,
            PerformanceReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.employeeRepository = employeeRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<PerformanceReviewResponse> saveOrUpdateReview(
            Long id, PerformanceReviewRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + request.getEmployeeId()));

        boolean isNew = (id == null);
        PerformanceReview review;

        if (isNew) {
            if (reviewRepository.existsByEmployee_IdAndReviewPeriod(
                    employee.getId(), request.getReviewPeriod())) {
                throw new DuplicateResourceException("Review for " + request.getReviewPeriod()
                        + " already exists for employee " + employee.getEmployeeCode());
            }
            review = new PerformanceReview();
        } else {
            review = findReviewOrThrow(id);

            if (reviewRepository.existsByEmployee_IdAndReviewPeriodAndIdNot(
                    employee.getId(), request.getReviewPeriod(), id)) {
                throw new DuplicateResourceException("Review for " + request.getReviewPeriod()
                        + " already exists for employee " + employee.getEmployeeCode());
            }
        }

        reviewMapper.mapToEntity(request, review);
        review.setEmployee(employee);

        PerformanceReview saved = reviewRepository.save(review);

        logger.info("Performance review {} for employee {} ({})", isNew ? "created" : "updated",
                employee.getEmployeeCode(), saved.getReviewPeriod());

        return ResponseEntity
                .status(isNew ? HttpStatus.CREATED : HttpStatus.OK)
                .body(reviewMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<PerformanceReviewResponse> getReviewById(Long id) {
        return ResponseEntity.ok(reviewMapper.toResponse(findReviewOrThrow(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<PerformanceReviewResponse>> getReviewsByEmployee(
            Long employeeId, int page, int size, String sortBy, String direction) {

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        Pageable pageable = PageUtil.buildPageable(page, size, sortBy, direction, SORT_FIELDS);

        return ResponseEntity.ok(reviewRepository.findByEmployee_Id(employeeId, pageable)
                .map(reviewMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getAverageRating(Long employeeId) {

        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }

        Double average = reviewRepository.findAverageRating(employeeId);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("employeeId", employeeId);
        body.put("averageRating", average == null ? 0.0 : Math.round(average * 100.0) / 100.0);

        return ResponseEntity.ok(body);
    }

    private PerformanceReview findReviewOrThrow(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Performance review not found with id: " + id));
    }
}
