package com.paycore.service;

import com.paycore.dto.EmployeeDto;
import com.paycore.entity.Employee;
import com.paycore.entity.Role;
import com.paycore.entity.User;
import com.paycore.repository.EmployeeRepository;
import com.paycore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public EmployeeDto getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return mapToDto(employee);
    }

    public EmployeeDto getEmployeeByUserId(Long userId) {
        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Employee record not found for user id: " + userId));
        return mapToDto(employee);
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto dto) {
        if (employeeRepository.existsByMobileNumber(dto.getMobileNumber())) {
            throw new IllegalArgumentException("Mobile number '" + dto.getMobileNumber() + "' is already registered.");
        }

        if (dto.getEmail() != null && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email address '" + dto.getEmail() + "' is already in use.");
        }

        String autoEmpCode = generateEmployeeCode();

        Role userRole = Role.ROLE_EMPLOYEE;
        if (dto.getRole() != null) {
            try {
                userRole = Role.valueOf(dto.getRole());
            } catch (Exception ignored) {}
        }

        String rawPassword = (dto.getPassword() != null && !dto.getPassword().isBlank()) ? dto.getPassword() : "Password123!";
        User newUser = new User(dto.getEmail(), passwordEncoder.encode(rawPassword), userRole);
        userRepository.save(newUser);

        LocalDate joining = (dto.getJoiningDate() != null) ? dto.getJoiningDate() : LocalDate.now();
        String dept = (dto.getDepartment() != null && !dto.getDepartment().isBlank()) ? dto.getDepartment() : "Engineering";

        Employee employee = new Employee(
                autoEmpCode,
                dto.getFirstName(),
                dto.getLastName(),
                dto.getDob(),
                dto.getMobileNumber(),
                dto.getDesignation(),
                dept,
                joining,
                newUser
        );

        Employee savedEmployee = employeeRepository.save(employee);

        // Dispatch welcome notification
        notificationService.createNotification(
                newUser,
                "Welcome to PayCore!",
                "Your employee profile has been created successfully. Employee ID: " + autoEmpCode
        );

        return mapToDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeDto dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        if (employeeRepository.existsByMobileNumberAndIdNot(dto.getMobileNumber(), id)) {
            throw new IllegalArgumentException("Mobile number '" + dto.getMobileNumber() + "' is already in use by another employee.");
        }

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setDob(dto.getDob());
        employee.setMobileNumber(dto.getMobileNumber());
        employee.setDesignation(dto.getDesignation());
        if (dto.getDepartment() != null) employee.setDepartment(dto.getDepartment());
        if (dto.getJoiningDate() != null) employee.setJoiningDate(dto.getJoiningDate());

        if (employee.getUser() != null && dto.getEmail() != null) {
            employee.getUser().setEmail(dto.getEmail());
        }

        Employee updated = employeeRepository.save(employee);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    private String generateEmployeeCode() {
        long count = employeeRepository.count() + 1001;
        return "EMP-" + count;
    }

    public EmployeeDto mapToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setEmployeeCode(employee.getEmployeeCode());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setDob(employee.getDob());
        dto.setMobileNumber(employee.getMobileNumber());
        dto.setDesignation(employee.getDesignation());
        dto.setDepartment(employee.getDepartment());
        dto.setJoiningDate(employee.getJoiningDate());
        if (employee.getUser() != null) {
            dto.setEmail(employee.getUser().getEmail());
            dto.setRole(employee.getUser().getRole().name());
        }
        return dto;
    }
}
