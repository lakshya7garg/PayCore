package com.paycore.service;

import com.paycore.dto.PayslipDto;
import com.paycore.dto.SalaryStructureDto;
import com.paycore.entity.*;
import com.paycore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    @Autowired
    private SalaryStructureRepository salaryStructureRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayslipRepository payslipRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private NotificationService notificationService;

    public List<SalaryStructureDto> getAllSalaryStructures() {
        return salaryStructureRepository.findAll()
                .stream()
                .map(this::mapStructureToDto)
                .collect(Collectors.toList());
    }

    public SalaryStructureDto getSalaryStructureByEmployeeId(Long employeeId) {
        SalaryStructure structure = salaryStructureRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Salary structure not defined for employee id: " + employeeId));
        return mapStructureToDto(structure);
    }

    @Transactional
    public SalaryStructureDto saveOrUpdateSalaryStructure(SalaryStructureDto dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.getEmployeeId()));

        Optional<SalaryStructure> existingOpt = salaryStructureRepository.findByEmployeeId(dto.getEmployeeId());

        SalaryStructure structure = existingOpt.orElseGet(() -> {
            SalaryStructure s = new SalaryStructure();
            s.setEmployee(employee);
            return s;
        });

        structure.setBasicSalary(dto.getBasicSalary());
        structure.setHra(dto.getHra());
        structure.setAllowances(dto.getAllowances());
        structure.setMedicalAllowance(dto.getMedicalAllowance());
        structure.setPfDeduction(dto.getPfDeduction());
        structure.setTaxDeduction(dto.getTaxDeduction());

        SalaryStructure saved = salaryStructureRepository.save(structure);
        return mapStructureToDto(saved);
    }

    @Transactional
    public PayslipDto generatePayslip(Long employeeId, Integer month, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        SalaryStructure structure = salaryStructureRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Cannot generate payslip: Salary structure not set for employee " + employee.getFirstName() + " " + employee.getLastName()));

        // Check if payslip already exists
        Optional<Payslip> existing = payslipRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // Count approved unpaid leave days in the target month
        Integer unpaidLeaveDays = leaveRequestRepository.countApprovedLeaveDaysByTypeAndDateRange(
                employeeId,
                LeaveRequest.LeaveType.UNPAID,
                startDate,
                endDate
        );

        BigDecimal basic = structure.getBasicSalary();
        BigDecimal hra = structure.getHra();
        BigDecimal allowances = structure.getAllowances().add(structure.getMedicalAllowance());
        BigDecimal grossSalary = basic.add(hra).add(allowances);

        // Calculate Daily Rate = Gross / 30
        BigDecimal dailyRate = grossSalary.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        BigDecimal unpaidLeaveDeduction = dailyRate.multiply(BigDecimal.valueOf(unpaidLeaveDays));

        BigDecimal pfDeduction = structure.getPfDeduction();
        BigDecimal taxDeduction = structure.getTaxDeduction();
        BigDecimal totalDeductions = pfDeduction.add(taxDeduction).add(unpaidLeaveDeduction);

        BigDecimal netPay = grossSalary.subtract(totalDeductions);
        if (netPay.compareTo(BigDecimal.ZERO) < 0) {
            netPay = BigDecimal.ZERO;
        }

        Payslip payslip = existing.orElseGet(Payslip::new);
        payslip.setEmployee(employee);
        payslip.setMonth(month);
        payslip.setYear(year);
        payslip.setBasicSalary(basic);
        payslip.setHra(hra);
        payslip.setAllowances(allowances);
        payslip.setGrossSalary(grossSalary);
        payslip.setPfDeduction(pfDeduction);
        payslip.setTaxDeduction(taxDeduction);
        payslip.setUnpaidLeaveDays(unpaidLeaveDays);
        payslip.setUnpaidLeaveDeduction(unpaidLeaveDeduction);
        payslip.setTotalDeductions(totalDeductions);
        payslip.setNetPay(netPay);

        Payslip savedPayslip = payslipRepository.save(payslip);

        // Send notification to employee
        if (employee.getUser() != null) {
            String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            notificationService.createNotification(
                    employee.getUser(),
                    "Payslip Generated",
                    "Your payslip for " + monthName + " " + year + " has been generated. Net Pay: ₹" + netPay
            );
        }

        return mapPayslipToDto(savedPayslip);
    }

    public List<PayslipDto> getEmployeePayslips(Long employeeId) {
        return payslipRepository.findByEmployeeIdOrderByYearDescMonthDesc(employeeId)
                .stream()
                .map(this::mapPayslipToDto)
                .collect(Collectors.toList());
    }

    public PayslipDto getPayslipById(Long payslipId) {
        Payslip payslip = payslipRepository.findById(payslipId)
                .orElseThrow(() -> new RuntimeException("Payslip not found with id: " + payslipId));
        return mapPayslipToDto(payslip);
    }

    private SalaryStructureDto mapStructureToDto(SalaryStructure s) {
        SalaryStructureDto dto = new SalaryStructureDto();
        dto.setId(s.getId());
        dto.setEmployeeId(s.getEmployee().getId());
        dto.setEmployeeName(s.getEmployee().getFirstName() + " " + s.getEmployee().getLastName());
        dto.setEmployeeCode(s.getEmployee().getEmployeeCode());
        dto.setBasicSalary(s.getBasicSalary());
        dto.setHra(s.getHra());
        dto.setAllowances(s.getAllowances());
        dto.setMedicalAllowance(s.getMedicalAllowance());
        dto.setPfDeduction(s.getPfDeduction());
        dto.setTaxDeduction(s.getTaxDeduction());

        BigDecimal gross = s.getBasicSalary().add(s.getHra()).add(s.getAllowances()).add(s.getMedicalAllowance());
        BigDecimal deductions = s.getPfDeduction().add(s.getTaxDeduction());
        dto.setTotalGross(gross);
        dto.setTotalNet(gross.subtract(deductions));

        return dto;
    }

    public PayslipDto mapPayslipToDto(Payslip p) {
        PayslipDto dto = new PayslipDto();
        dto.setId(p.getId());
        dto.setEmployeeId(p.getEmployee().getId());
        dto.setEmployeeCode(p.getEmployee().getEmployeeCode());
        dto.setEmployeeName(p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName());
        dto.setDesignation(p.getEmployee().getDesignation());
        dto.setDepartment(p.getEmployee().getDepartment());
        dto.setMonth(p.getMonth());
        dto.setYear(p.getYear());
        dto.setMonthName(Month.of(p.getMonth()).getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        dto.setBasicSalary(p.getBasicSalary());
        dto.setHra(p.getHra());
        dto.setAllowances(p.getAllowances());
        dto.setGrossSalary(p.getGrossSalary());
        dto.setPfDeduction(p.getPfDeduction());
        dto.setTaxDeduction(p.getTaxDeduction());
        dto.setUnpaidLeaveDays(p.getUnpaidLeaveDays());
        dto.setUnpaidLeaveDeduction(p.getUnpaidLeaveDeduction());
        dto.setTotalDeductions(p.getTotalDeductions());
        dto.setNetPay(p.getNetPay());
        dto.setGeneratedAt(p.getGeneratedAt());
        return dto;
    }
}
