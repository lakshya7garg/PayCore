package com.paycore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payslips")
public class Payslip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "month", nullable = false)
    private Integer month;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "basic_salary", nullable = false)
    private BigDecimal basicSalary;

    @Column(name = "hra", nullable = false)
    private BigDecimal hra;

    @Column(name = "allowances", nullable = false)
    private BigDecimal allowances;

    @Column(name = "gross_salary", nullable = false)
    private BigDecimal grossSalary;

    @Column(name = "pf_deduction", nullable = false)
    private BigDecimal pfDeduction;

    @Column(name = "tax_deduction", nullable = false)
    private BigDecimal taxDeduction;

    @Column(name = "unpaid_leave_days", nullable = false)
    private Integer unpaidLeaveDays;

    @Column(name = "unpaid_leave_deduction", nullable = false)
    private BigDecimal unpaidLeaveDeduction;

    @Column(name = "total_deductions", nullable = false)
    private BigDecimal totalDeductions;

    @Column(name = "net_pay", nullable = false)
    private BigDecimal netPay;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    public Payslip() {
        this.generatedAt = LocalDateTime.now();
    }

    public Payslip(Employee employee, Integer month, Integer year, BigDecimal basicSalary, BigDecimal hra, BigDecimal allowances, BigDecimal grossSalary, BigDecimal pfDeduction, BigDecimal taxDeduction, Integer unpaidLeaveDays, BigDecimal unpaidLeaveDeduction, BigDecimal totalDeductions, BigDecimal netPay) {
        this.employee = employee;
        this.month = month;
        this.year = year;
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.allowances = allowances;
        this.grossSalary = grossSalary;
        this.pfDeduction = pfDeduction;
        this.taxDeduction = taxDeduction;
        this.unpaidLeaveDays = unpaidLeaveDays;
        this.unpaidLeaveDeduction = unpaidLeaveDeduction;
        this.totalDeductions = totalDeductions;
        this.netPay = netPay;
        this.generatedAt = LocalDateTime.now();
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

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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

    public BigDecimal getGrossSalary() {
        return grossSalary;
    }

    public void setGrossSalary(BigDecimal grossSalary) {
        this.grossSalary = grossSalary;
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

    public Integer getUnpaidLeaveDays() {
        return unpaidLeaveDays;
    }

    public void setUnpaidLeaveDays(Integer unpaidLeaveDays) {
        this.unpaidLeaveDays = unpaidLeaveDays;
    }

    public BigDecimal getUnpaidLeaveDeduction() {
        return unpaidLeaveDeduction;
    }

    public void setUnpaidLeaveDeduction(BigDecimal unpaidLeaveDeduction) {
        this.unpaidLeaveDeduction = unpaidLeaveDeduction;
    }

    public BigDecimal getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(BigDecimal totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public BigDecimal getNetPay() {
        return netPay;
    }

    public void setNetPay(BigDecimal netPay) {
        this.netPay = netPay;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
