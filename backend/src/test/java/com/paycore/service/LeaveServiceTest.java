package com.paycore.service;

import com.paycore.dto.LeaveRequestDto;
import com.paycore.entity.Employee;
import com.paycore.entity.LeaveRequest;
import com.paycore.repository.EmployeeRepository;
import com.paycore.repository.LeaveRequestRepository;
import com.paycore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LeaveService leaveService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee("EMP-1002", "Bob", "Marley", LocalDate.of(1991, 5, 5), "9887766554", "QA Engineer", "Testing", LocalDate.now(), null);
        employee.setId(2L);
    }

    @Test
    void testApplyForLeave_Success() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setLeaveType(LeaveRequest.LeaveType.PAID);
        dto.setStartDate(LocalDate.of(2026, 8, 1));
        dto.setEndDate(LocalDate.of(2026, 8, 5));
        dto.setReason("Vacation");

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenAnswer(i -> {
            LeaveRequest req = i.getArgument(0);
            req.setId(100L);
            return req;
        });
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        LeaveRequestDto result = leaveService.applyForLeave(2L, dto);

        assertNotNull(result);
        assertEquals(5, result.getDaysCount()); // Aug 1 to Aug 5 inclusive = 5 days
        assertEquals(LeaveRequest.LeaveStatus.PENDING, result.getStatus());
        verify(leaveRequestRepository, times(1)).save(any());
    }

    @Test
    void testApplyForLeave_InvalidDateRange_ThrowsException() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setStartDate(LocalDate.of(2026, 8, 5));
        dto.setEndDate(LocalDate.of(2026, 8, 1));

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee));

        assertThrows(IllegalArgumentException.class, () -> leaveService.applyForLeave(2L, dto));
        verify(leaveRequestRepository, never()).save(any());
    }
}
