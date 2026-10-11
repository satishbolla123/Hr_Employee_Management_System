package com.hr.employeemanagement.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import com.hr.employeemanagement.dto.request.EmployeeRequest;
import com.hr.employeemanagement.dto.response.EmployeeResponse;
import com.hr.employeemanagement.entity.Department;
import com.hr.employeemanagement.entity.Employee;
import com.hr.employeemanagement.entity.Skill;
import com.hr.employeemanagement.enums.EmployeeStatus;
import com.hr.employeemanagement.exception.DuplicateResourceException;
import com.hr.employeemanagement.exception.InvalidRequestException;
import com.hr.employeemanagement.exception.ResourceNotFoundException;
import com.hr.employeemanagement.mapper.EmployeeMapper;
import com.hr.employeemanagement.repository.DepartmentRepository;
import com.hr.employeemanagement.repository.EmployeeRepository;
import com.hr.employeemanagement.repository.SkillRepository;
import com.hr.employeemanagement.service.EmployeeService;
import com.hr.employeemanagement.util.PageUtil;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private static final List<String> SORT_FIELDS = List.of(
            "id", "employeeCode", "firstName", "lastName", "email", "hireDate", "designation", "salary");

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final SkillRepository skillRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            SkillRepository skillRepository,
            EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.skillRepository = skillRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"employees", "departments"}, allEntries = true)
    public ResponseEntity<EmployeeResponse> saveOrUpdateEmployee(Long id, EmployeeRequest request) {

        boolean isNew = (id == null);
        Employee employee;

        if (isNew) {
            if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
                throw new DuplicateResourceException(
                        "Employee code already exists: " + request.getEmployeeCode());
            }
            if (employeeRepository.existsByEmailIgnoreCase(request.getEmail().trim())) {
                throw new DuplicateResourceException(
                        "Email already registered: " + request.getEmail());
            }
            employee = new Employee();
            employee.setStatus(EmployeeStatus.ACTIVE);
        } else {
            employee = findEmployeeOrThrow(id);

            if (employeeRepository.existsByEmployeeCodeAndIdNot(request.getEmployeeCode(), id)) {
                throw new DuplicateResourceException(
                        "Employee code already used by someone else: " + request.getEmployeeCode());
            }
            if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail().trim(), id)) {
                throw new DuplicateResourceException(
                        "Email already used by someone else: " + request.getEmail());
            }
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + request.getDepartmentId()));

        employeeMapper.mapToEntity(request, employee);
        employee.setDepartment(department);
        employee.setSkills(resolveSkills(request.getSkills()));

        Employee saved = employeeRepository.save(employee);

        logger.info("Employee {} with id {}", isNew ? "created" : "updated", saved.getId());

        return ResponseEntity
                .status(isNew ? HttpStatus.CREATED : HttpStatus.OK)
                .body(employeeMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "employees", key = "'employee:' + #id")
    public ResponseEntity<EmployeeResponse> getEmployeeById(Long id) {

        logger.debug("Loading employee {} from the database", id);

        return ResponseEntity.ok(employeeMapper.toResponse(findEmployeeOrThrow(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            int page, int size, String sortBy, String direction) {

        Pageable pageable = PageUtil.buildPageable(page, size, sortBy, direction, SORT_FIELDS);

        return ResponseEntity.ok(employeeRepository.findAll(pageable).map(employeeMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<EmployeeResponse>> searchEmployees(
            String keyword, int page, int size, String sortBy, String direction) {

        if (keyword == null || keyword.isBlank()) {
            throw new InvalidRequestException("Search keyword cannot be empty");
        }

        Pageable pageable = PageUtil.buildPageable(page, size, sortBy, direction, SORT_FIELDS);

        return ResponseEntity.ok(employeeRepository.searchByKeyword(keyword.trim(), pageable)
                .map(employeeMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Page<EmployeeResponse>> getEmployeesByDepartment(
            Long departmentId, int page, int size, String sortBy, String direction) {

        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found with id: " + departmentId);
        }

        Pageable pageable = PageUtil.buildPageable(page, size, sortBy, direction, SORT_FIELDS);

        return ResponseEntity.ok(employeeRepository.findByDepartment_Id(departmentId, pageable)
                .map(employeeMapper::toResponse));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"employees", "departments"}, allEntries = true)
    public ResponseEntity<EmployeeResponse> deactivateEmployee(Long id) {

        Employee employee = findEmployeeOrThrow(id);

        // we keep the record because attendance, leave and review history depend on it
        employee.setStatus(EmployeeStatus.INACTIVE);

        Employee saved = employeeRepository.save(employee);

        logger.info("Employee {} marked as inactive", id);

        return ResponseEntity.ok(employeeMapper.toResponse(saved));
    }

    private Set<Skill> resolveSkills(Set<String> skillNames) {

        Set<Skill> skills = new HashSet<>();

        if (skillNames == null) {
            return skills;
        }

        for (String rawName : skillNames) {

            if (rawName == null || rawName.isBlank()) {
                continue;
            }

            String name = rawName.trim();

            Skill skill = skillRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> {
                        Skill newSkill = new Skill();
                        newSkill.setName(name);
                        return skillRepository.save(newSkill);
                    });

            skills.add(skill);
        }

        return skills;
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id));
    }
}
