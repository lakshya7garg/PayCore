package com.paycore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SalaryStructureDto {

    private Long id;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String employeeName;
    private String employeeCode;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(value = "0.0", message = "Basic salary cannot be negative")
    private BigDecimal basicSalary;

    @NotNull(message = "HRA is required")
    @DecimalMin(value = "0.0", message = "HRA cannot be negative")
    private BigDecimal hra;

    @NotNull(message = "Allowances is required")
    @DecimalMin(value = "0.0", message = "Allowances cannot be negative")
    private BigDecimal allowances;

    @NotNull(message = "Medical allowance is required")
    @DecimalMin(value = "0.0", message = "Medical allowance cannot be negative")
    private BigDecimal medicalAllowance;

    @NotNull(message = "PF deduction is required")
    @DecimalMin(value = "0.0", message = "PF deduction cannot be negative")
    private BigDecimal pfDeduction;

    @NotNull(message = "Tax deduction is required")
    @DecimalMin(value = "0.0", message = "Tax deduction cannot be negative")
    private BigDecimal taxDeduction;

    private BigDecimal totalGross;
    private BigDecimal totalNet;

    public SalaryStructureDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public BigDecimal getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(BigDecimal basicSalary) {
        this.basicSalary = basicSalary;
    }

    public BigDecimal getHra() {
        return hra;
    }

    public void setHra(BigDecimal hra) {
        this.hra = hra;
    }

    public BigDecimal getAllowances() {
        return allowances;
    }

    public void setAllowances(BigDecimal allowances) {
        this.allowances = allowances;
    }

    public BigDecimal getMedicalAllowance() {
        return medicalAllowance;
    }

    public void setMedicalAllowance(BigDecimal medicalAllowance) {
        this.medicalAllowance = medicalAllowance;
    }

    public BigDecimal getPfDeduction() {
        return pfDeduction;
    }

    public void setPfDeduction(BigDecimal pfDeduction) {
        this.pfDeduction = pfDeduction;
    }

    public BigDecimal getTaxDeduction() {
        return taxDeduction;
    }

    public void setTaxDeduction(BigDecimal taxDeduction) {
        this.taxDeduction = taxDeduction;
    }

    public BigDecimal getTotalGross() {
        return totalGross;
    }

    public void setTotalGross(BigDecimal totalGross) {
        this.totalGross = totalGross;
    }

    public BigDecimal getTotalNet() {
        return totalNet;
    }

    public void setTotalNet(BigDecimal totalNet) {
        this.totalNet = totalNet;
    }
}
