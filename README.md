# HR Employee Management

Spring Boot REST project to manage employees, departments, attendance, leave and performance reviews.

## Tech used
Java 17, Spring Boot 4.1.1, Spring Data JPA (Hibernate), MySQL, Spring Security (basic auth),
Caffeine cache, Actuator, springdoc Swagger UI, JUnit 5 + Mockito.

## How to run
1. Start MySQL. The database `hr_employee_management` is created automatically on first start.
2. Copy `src/main/resources/local.properties.example` to `local.properties` and put your MySQL password and the two login passwords in it (this file is not pushed to Git).
3. Run `./mvnw spring-boot:run` (or run `HrEmployeeManagementApplication` from the IDE).
4. Open http://localhost:8080/swagger-ui.html and use **Authorize** with a login below.

| Role     | Username   | Password       | Can do                                   |
|----------|------------|----------------|------------------------------------------|
| ADMIN    | `admin`    | set in local.properties | everything, including approve/reject leave |
| EMPLOYEE | `employee` | set in local.properties | view data, apply leave, mark attendance  |

## Modules and relationships
- Department 1 -> * Employee (one to many / many to one)
- Employee 1 -> 1 EmployeeProfile (one to one)
- Employee * <-> * Skill (many to many, table `employee_skills`)
- Employee 1 -> * Attendance, LeaveRequest, PerformanceReview

## Main APIs
| Module | Endpoints |
|--------|-----------|
| Departments | `POST/PUT/GET/DELETE /api/departments`, `GET /api/departments/summary` |
| Employees | `POST/PUT/GET/DELETE /api/employees`, `GET /api/employees/search?keyword=`, `GET /api/employees/department/{id}` |
| Attendance | `POST/PUT /api/attendance`, `GET /api/attendance/search?employeeId=&fromDate=&toDate=&status=` |
| Leave | `POST /api/leaves`, `PUT /api/leaves/{id}/approve`, `PUT /api/leaves/{id}/reject`, `GET /api/leaves?status=` |
| Reviews | `POST/PUT/GET /api/reviews`, `GET /api/reviews/employee/{id}/average` |

List APIs support `page`, `size`, `sortBy` and `direction` (asc / desc).

## Good to know
- Deleting an employee only marks them INACTIVE, so attendance / leave / review history is kept.
- Approving a leave marks the working days as ON_LEAVE in attendance (single transaction).
- A scheduler marks ABSENT for active employees with no attendance, Mon-Fri at 11:30 PM.
- Logs are also written to `logs/hr-employee-management.log`.
- Run the test with `./mvnw test`.
