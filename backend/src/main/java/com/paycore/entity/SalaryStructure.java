package com.paycore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_structures")
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "basic_salary", nullable = false)
    private BigDecimal basicSalary;

    @Column(name = "hra", nullable = false)
    private BigDecimal hra;

    @Column(name = "allowances", nullable = false)
    private BigDecimal allowances;

    @Column(name = "medical_allowance", nullable = false)
    private BigDecimal medicalAllowance;

    @Column(name = "pf_deduction", nullable = false)
    private BigDecimal pfDeduction;

    @Column(name = "tax_deduction", nullable = false)
    private BigDecimal taxDeduction;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SalaryStructure() {
        this.updatedAt = LocalDateTime.now();
    }

    public SalaryStructure(Employee employee, BigDecimal basicSalary, BigDecimal hra, BigDecimal allowances, BigDecimal medicalAllowance, BigDecimal pfDeduction, BigDecimal taxDeduction) {
        this.employee = employee;
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.allowances = allowances;
        this.medicalAllowance = medicalAllowance;
        this.pfDeduction = pfDeduction;
        this.taxDeduction = taxDeduction;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
