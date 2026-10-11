package com.hr.employeemanagement.mapper;

import org.springframework.stereotype.Component;

import com.hr.employeemanagement.dto.request.PerformanceReviewRequest;
import com.hr.employeemanagement.dto.response.PerformanceReviewResponse;
import com.hr.employeemanagement.entity.PerformanceReview;

@Component
public class PerformanceReviewMapper {

    public void mapToEntity(PerformanceReviewRequest request, PerformanceReview review) {

        review.setReviewPeriod(request.getReviewPeriod());
        review.setRating(request.getRating());
        review.setStrengths(request.getStrengths());
        review.setImprovementAreas(request.getImprovementAreas());
        review.setComments(request.getComments());
        review.setReviewDate(request.getReviewDate());
    }

    public PerformanceReviewResponse toResponse(PerformanceReview review) {

        PerformanceReviewResponse response = new PerformanceReviewResponse();

        response.setId(review.getId());
        response.setEmployeeId(review.getEmployee().getId());
        response.setEmployeeName(review.getEmployee().getFirstName()
                + " " + review.getEmployee().getLastName());
        response.setReviewPeriod(review.getReviewPeriod());
        response.setRating(review.getRating());
        response.setStrengths(review.getStrengths());
        response.setImprovementAreas(review.getImprovementAreas());
        response.setComments(review.getComments());
        response.setReviewDate(review.getReviewDate());

        return response;
    }
}
