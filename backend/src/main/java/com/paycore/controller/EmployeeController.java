package com.paycore.controller;

import com.paycore.dto.ApiResponse;
import com.paycore.dto.EmployeeDto;
import com.paycore.entity.User;
import com.paycore.repository.UserRepository;
import com.paycore.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<ApiResponse<List<EmployeeDto>>> getAllEmployees() {
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(new ApiResponse<>(true, "Employees retrieved successfully", employees));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> getEmployeeById(@PathVariable Long id) {
        EmployeeDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Employee details retrieved", employee));
    }

    @GetMapping("/profile/me")
    public ResponseEntity<ApiResponse<EmployeeDto>> getSelfProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmployeeDto employee = employeeService.getEmployeeByUserId(user.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile details retrieved", employee));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> createEmployee(@Valid @RequestBody EmployeeDto dto) {
        try {
            EmployeeDto created = employeeService.createEmployee(dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Employee created successfully", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeDto>> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeDto dto) {
        try {
            EmployeeDto updated = employeeService.updateEmployee(id, dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Employee updated successfully", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Employee deleted successfully"));
    }
}
