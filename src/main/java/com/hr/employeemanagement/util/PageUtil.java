package com.hr.employeemanagement.util;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.hr.employeemanagement.exception.InvalidRequestException;

public final class PageUtil {

    private PageUtil() {
    }

    public static Pageable buildPageable(int page, int size, String sortBy,
            String direction, List<String> allowedSortFields) {

        if (page < 0) {
            throw new InvalidRequestException("Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new InvalidRequestException("Page size must be between 1 and 100");
        }

        if (!allowedSortFields.contains(sortBy)) {
            throw new InvalidRequestException(
                    "Cannot sort by '" + sortBy + "'. Allowed fields: " + allowedSortFields);
        }

        Sort.Direction sortDirection;

        if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new InvalidRequestException("Sort direction must be 'asc' or 'desc'");
        }

        return PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
    }
}
