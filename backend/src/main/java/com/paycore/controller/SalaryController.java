package com.paycore.controller;

import com.paycore.dto.ApiResponse;
import com.paycore.dto.PayslipDto;
import com.paycore.dto.SalaryStructureDto;
import com.paycore.entity.User;
import com.paycore.repository.UserRepository;
import com.paycore.service.EmployeeService;
import com.paycore.service.SalaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/structures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SalaryStructureDto>>> getAllSalaryStructures() {
        List<SalaryStructureDto> structures = salaryService.getAllSalaryStructures();
        return ResponseEntity.ok(new ApiResponse<>(true, "Salary structures retrieved", structures));
    }

    @GetMapping("/structures/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SalaryStructureDto>> getStructureByEmployee(@PathVariable Long employeeId) {
        SalaryStructureDto structure = salaryService.getSalaryStructureByEmployeeId(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Salary structure retrieved", structure));
    }

    @PostMapping("/structures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SalaryStructureDto>> saveOrUpdateStructure(@Valid @RequestBody SalaryStructureDto dto) {
        SalaryStructureDto saved = salaryService.saveOrUpdateSalaryStructure(dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Salary structure saved successfully", saved));
    }

    @PostMapping("/payslips/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PayslipDto>> generatePayslip(
            @RequestParam Long employeeId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        try {
            PayslipDto payslip = salaryService.generatePayslip(employeeId, month, year);
            return ResponseEntity.ok(new ApiResponse<>(true, "Payslip generated successfully", payslip));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/payslips/my")
    public ResponseEntity<ApiResponse<List<PayslipDto>>> getMyPayslips() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long employeeId = employeeService.getEmployeeByUserId(user.getId()).getId();
        List<PayslipDto> payslips = salaryService.getEmployeePayslips(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "My payslips retrieved", payslips));
    }

    @GetMapping("/payslips/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PayslipDto>>> getEmployeePayslips(@PathVariable Long employeeId) {
        List<PayslipDto> payslips = salaryService.getEmployeePayslips(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Employee payslips retrieved", payslips));
    }

    @GetMapping("/payslips/{id}")
    public ResponseEntity<ApiResponse<PayslipDto>> getPayslipById(@PathVariable Long id) {
        PayslipDto payslip = salaryService.getPayslipById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payslip details retrieved", payslip));
    }
}
