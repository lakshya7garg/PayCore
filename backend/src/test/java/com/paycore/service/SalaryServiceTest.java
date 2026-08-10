package com.paycore.service;

import com.paycore.dto.PayslipDto;
import com.paycore.entity.Employee;
import com.paycore.entity.LeaveRequest;
import com.paycore.entity.Payslip;
import com.paycore.entity.SalaryStructure;
import com.paycore.repository.EmployeeRepository;
import com.paycore.repository.LeaveRequestRepository;
import com.paycore.repository.PayslipRepository;
import com.paycore.repository.SalaryStructureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalaryServiceTest {

    @Mock
    private SalaryStructureRepository salaryStructureRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PayslipRepository payslipRepository;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private SalaryService salaryService;

    private Employee employee;
    private SalaryStructure salaryStructure;

    @BeforeEach
    void setUp() {
        employee = new Employee("EMP-1001", "Alice", "Smith", LocalDate.of(1995, 2, 10), "9998887770", "Developer", "IT", LocalDate.now(), null);
        employee.setId(1L);

        salaryStructure = new SalaryStructure(
                employee,
                new BigDecimal("6000.00"), // Basic
                new BigDecimal("2000.00"), // HRA
                new BigDecimal("1000.00"), // Allowances
                new BigDecimal("0.00"),    // Medical
                new BigDecimal("500.00"),  // PF
                new BigDecimal("500.00")   // Tax
        ); // Gross = 9000. Daily rate = 9000/30 = 300
    }

    @Test
    void testGeneratePayslip_WithUnpaidLeaveDeductions() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salaryStructureRepository.findByEmployeeId(1L)).thenReturn(Optional.of(salaryStructure));
        when(payslipRepository.findByEmployeeIdAndMonthAndYear(1L, 7, 2026)).thenReturn(Optional.empty());

        // 2 unpaid leave days => 2 * 300 = 600 deduction
        when(leaveRequestRepository.countApprovedLeaveDaysByTypeAndDateRange(
                eq(1L), eq(LeaveRequest.LeaveType.UNPAID), any(), any()))
                .thenReturn(2);

        when(payslipRepository.save(any(Payslip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayslipDto payslip = salaryService.generatePayslip(1L, 7, 2026);

        assertNotNull(payslip);
        assertEquals(new BigDecimal("9000.00"), payslip.getGrossSalary());
        assertEquals(2, payslip.getUnpaidLeaveDays());
        assertEquals(new BigDecimal("600.00"), payslip.getUnpaidLeaveDeduction());
        assertEquals(new BigDecimal("1600.00"), payslip.getTotalDeductions()); // 500 PF + 500 Tax + 600 Leave = 1600
        assertEquals(new BigDecimal("7400.00"), payslip.getNetPay());         // 9000 - 1600 = 7400
    }
}
