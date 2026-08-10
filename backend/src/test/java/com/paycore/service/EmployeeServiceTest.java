package com.paycore.service;

import com.paycore.dto.EmployeeDto;
import com.paycore.entity.Employee;
import com.paycore.entity.Role;
import com.paycore.entity.User;
import com.paycore.repository.EmployeeRepository;
import com.paycore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee sampleEmployee;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User("john.doe@paycore.com", "encodedPass", Role.ROLE_EMPLOYEE);
        sampleUser.setId(1L);

        sampleEmployee = new Employee(
                "EMP-1001",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "9876543210",
                "Software Engineer",
                "Engineering",
                LocalDate.of(2023, 1, 1),
                sampleUser
        );
        sampleEmployee.setId(10L);
    }

    @Test
    void testGetEmployeeById_Success() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(sampleEmployee));

        EmployeeDto dto = employeeService.getEmployeeById(10L);

        assertNotNull(dto);
        assertEquals("EMP-1001", dto.getEmployeeCode());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("john.doe@paycore.com", dto.getEmail());
    }

    @Test
    void testCreateEmployee_DuplicateMobileNumber_ThrowsException() {
        EmployeeDto dto = new EmployeeDto();
        dto.setMobileNumber("9876543210");
        dto.setFirstName("Jane");
        dto.setLastName("Doe");

        when(employeeRepository.existsByMobileNumber("9876543210")).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.createEmployee(dto);
        });

        assertTrue(exception.getMessage().contains("already registered"));
        verify(employeeRepository, never()).save(any());
    }
}
