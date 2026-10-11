package com.hr.employeemanagement.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.hr.employeemanagement.dto.request.EmployeeRequest;
import com.hr.employeemanagement.dto.response.EmployeeResponse;
import com.hr.employeemanagement.exception.GlobalExceptionHandler;
import com.hr.employeemanagement.exception.ResourceNotFoundException;
import com.hr.employeemanagement.service.EmployeeService;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createEmployee_withValidRequest_returnsCreated() throws Exception {

        EmployeeResponse response = new EmployeeResponse();
        response.setId(1L);
        response.setEmployeeCode("EMP-001");
        response.setFirstName("Ravi");
        response.setLastName("Kumar");

        when(employeeService.saveOrUpdateEmployee(isNull(), any(EmployeeRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(response));

        String json = """
                {
                  "employeeCode": "EMP-001",
                  "firstName": "Ravi",
                  "lastName": "Kumar",
                  "email": "ravi.kumar@example.com",
                  "phoneNumber": "9876543210",
                  "hireDate": "2024-01-15",
                  "designation": "Software Engineer",
                  "salary": 55000,
                  "departmentId": 1
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("EMP-001")));
    }

    @Test
    void createEmployee_withInvalidRequest_returnsBadRequest() throws Exception {

        // email is wrong and most mandatory fields are missing
        String json = """
                {
                  "firstName": "Ravi",
                  "email": "not-an-email"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Validation Failed")));

        verifyNoInteractions(employeeService);
    }

    @Test
    void getEmployeeById_whenMissing_returnsNotFound() throws Exception {

        when(employeeService.getEmployeeById(99L))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: 99"));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Employee not found with id: 99")));
    }
}
