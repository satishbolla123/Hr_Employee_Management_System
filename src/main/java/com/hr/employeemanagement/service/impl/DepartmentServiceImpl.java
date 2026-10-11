package com.hr.employeemanagement.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hr.employeemanagement.dto.request.DepartmentRequest;
import com.hr.employeemanagement.dto.response.DepartmentResponse;
import com.hr.employeemanagement.dto.response.DepartmentSummaryResponse;
import com.hr.employeemanagement.entity.Department;
import com.hr.employeemanagement.exception.DuplicateResourceException;
import com.hr.employeemanagement.exception.InvalidRequestException;
import com.hr.employeemanagement.exception.ResourceNotFoundException;
import com.hr.employeemanagement.mapper.DepartmentMapper;
import com.hr.employeemanagement.repository.DepartmentRepository;
import com.hr.employeemanagement.service.DepartmentService;
import com.hr.employeemanagement.util.PageUtil;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private static final List<String> SORT_FIELDS = List.of("id", "name", "location");

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
            DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "departments", allEntries = true)
    public ResponseEntity<DepartmentResponse> saveOrUpdateDepartment(
            Long id, DepartmentRequest request) {

        boolean isNew = (id == null);
        Department department;

        if (isNew) {
            if (departmentRepository.existsByNameIgnoreCase(request.getName().trim())) {
                throw new DuplicateResourceException(
                        "Department already exists with name: " + request.getName());
            }
            department = new Department();
        } else {
            department = findDepartmentOrThrow(id);

            if (departmentRepository.existsByNameIgnoreCaseAndIdNot(request.getName().trim(), id)) {
                throw new DuplicateResourceException(
                        "Another department already uses the name: " + request.getName());
            }
        }

        departmentMapper.mapToEntity(request, department);

        Department saved = departmentRepository.save(department);

        logger.info("Department {} with id {}", isNew ? "created" : "updated", saved.getId());

        return ResponseEntity
                .status(isNew ? HttpStatus.CREATED : HttpStatus.OK)
                .body(departmentMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "departments", key = "'department:' + #id")
    public ResponseEntity<DepartmentResponse> getDepartmentById(Long id) {

        logger.debug("Loading department {} from the database", id);

        return ResponseEntity.ok(departmentMapper.toResponse(findDepartmentOrThrow(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(
            int page, int size, String sortBy, String direction) {

        Pageable pageable = PageUtil.buildPageable(page, size, sortBy, direction, SORT_FIELDS);

        Page<DepartmentResponse> result = departmentRepository.findAll(pageable)
                .map(departmentMapper::toResponse);

        return ResponseEntity.ok(result);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<DepartmentSummaryResponse>> getDepartmentSummary() {

        List<DepartmentSummaryResponse> summary = departmentRepository.getDepartmentSummary()
                .stream()
                .map(departmentMapper::toSummaryResponse)
                .toList();

        return ResponseEntity.ok(summary);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "departments", allEntries = true)
    public ResponseEntity<String> deleteDepartment(Long id) {

        Department department = findDepartmentOrThrow(id);

        if (!department.getEmployees().isEmpty()) {
            throw new InvalidRequestException(
                    "Department cannot be deleted while it still has employees. Move them first");
        }

        departmentRepository.delete(department);

        logger.info("Department {} deleted", id);

        return ResponseEntity.ok("Department deleted successfully");
    }

    private Department findDepartmentOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + id));
    }
}
