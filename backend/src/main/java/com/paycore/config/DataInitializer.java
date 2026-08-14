package com.paycore.config;

import com.paycore.entity.*;
import com.paycore.repository.*;
import com.paycore.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SalaryStructureRepository salaryStructureRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SalaryService salaryService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println(">>> Initializing PayCore Default Database Seed Data...");

            // 1. Seed Admin User & Employee Profile
            User adminUser = new User("admin@paycore.com", passwordEncoder.encode("Password123!"), Role.ROLE_ADMIN);
            userRepository.save(adminUser);

            Employee adminEmp = new Employee(
                    "EMP-1001",
                    "System",
                    "Administrator",
                    LocalDate.of(1985, 5, 15),
                    "9876543210",
                    "HR Admin Lead",
                    "Executive",
                    LocalDate.of(2020, 1, 1),
                    adminUser
            );
            employeeRepository.save(adminEmp);

            SalaryStructure adminSalary = new SalaryStructure(
                    adminEmp,
                    new BigDecimal("7500.00"),
                    new BigDecimal("2500.00"),
                    new BigDecimal("1500.00"),
                    new BigDecimal("500.00"),
                    new BigDecimal("600.00"),
                    new BigDecimal("900.00")
            );
            salaryStructureRepository.save(adminSalary);

            // 2. Seed Employee User & Profile
            User empUser = new User("employee@paycore.com", passwordEncoder.encode("Password123!"), Role.ROLE_EMPLOYEE);
            userRepository.save(empUser);

            Employee regularEmp = new Employee(
                    "EMP-1002",
                    "Sarah",
                    "Jenkins",
                    LocalDate.of(1993, 8, 22),
                    "9876543211",
                    "Senior Software Engineer",
                    "Engineering",
                    LocalDate.of(2022, 3, 15),
                    empUser
            );
            employeeRepository.save(regularEmp);

            SalaryStructure empSalary = new SalaryStructure(
                    regularEmp,
                    new BigDecimal("5500.00"),
                    new BigDecimal("1800.00"),
                    new BigDecimal("1200.00"),
                    new BigDecimal("400.00"),
                    new BigDecimal("440.00"),
                    new BigDecimal("650.00")
            );
            salaryStructureRepository.save(empSalary);

            // 4. Seed Sample Leave Request
            LeaveRequest leave1 = new LeaveRequest(
                    regularEmp,
                    LeaveRequest.LeaveType.UNPAID,
                    LocalDate.of(2026, 7, 10),
                    LocalDate.of(2026, 7, 11),
                    2,
                    "Family emergency trip"
            );
            leave1.setStatus(LeaveRequest.LeaveStatus.APPROVED);
            leaveRequestRepository.save(leave1);

            // 5. Generate sample payslips
            salaryService.generatePayslip(regularEmp.getId(), 7, 2026);
            salaryService.generatePayslip(adminEmp.getId(), 7, 2026);

            System.out.println(">>> PayCore Seed Data Successfully Initialized!");
        }
    }
}
