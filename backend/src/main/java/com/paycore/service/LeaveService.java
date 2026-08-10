package com.paycore.service;

import com.paycore.dto.LeaveRequestDto;
import com.paycore.entity.Employee;
import com.paycore.entity.LeaveRequest;
import com.paycore.entity.Role;
import com.paycore.entity.User;
import com.paycore.repository.EmployeeRepository;
import com.paycore.repository.LeaveRequestRepository;
import com.paycore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public List<LeaveRequestDto> getAllLeaveRequests() {
        return leaveRequestRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<LeaveRequestDto> getEmployeeLeaveRequests(Long employeeId) {
        return leaveRequestRepository.findByEmployeeIdOrderByAppliedAtDesc(employeeId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaveRequestDto applyForLeave(Long employeeId, LeaveRequestDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("Leave end date cannot be prior to start date.");
        }

        int daysCount = (int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;

        LeaveRequest leaveRequest = new LeaveRequest(
                employee,
                dto.getLeaveType(),
                dto.getStartDate(),
                dto.getEndDate(),
                daysCount,
                dto.getReason()
        );

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        // Notify Admins about new leave request
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_ADMIN)
                .collect(Collectors.toList());

        for (User admin : admins) {
            notificationService.createNotification(
                    admin,
                    "New Leave Request",
                    employee.getFirstName() + " " + employee.getLastName() + " applied for " + daysCount + " day(s) of " + dto.getLeaveType() + " leave."
            );
        }

        return mapToDto(saved);
    }

    @Transactional
    public LeaveRequestDto reviewLeaveRequest(Long leaveRequestId, LeaveRequest.LeaveStatus status, String comments) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with id: " + leaveRequestId));

        leaveRequest.setStatus(status);
        leaveRequest.setManagerComments(comments);
        leaveRequest.setReviewedAt(LocalDateTime.now());

        LeaveRequest updated = leaveRequestRepository.save(leaveRequest);

        // Notify Employee about decision
        if (leaveRequest.getEmployee().getUser() != null) {
            notificationService.createNotification(
                    leaveRequest.getEmployee().getUser(),
                    "Leave Request " + status.name(),
                    "Your leave request from " + leaveRequest.getStartDate() + " to " + leaveRequest.getEndDate() + " has been " + status.name().toLowerCase() + "."
            );
        }

        return mapToDto(updated);
    }

    private LeaveRequestDto mapToDto(LeaveRequest l) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(l.getId());
        dto.setEmployeeId(l.getEmployee().getId());
        dto.setEmployeeName(l.getEmployee().getFirstName() + " " + l.getEmployee().getLastName());
        dto.setEmployeeCode(l.getEmployee().getEmployeeCode());
        dto.setLeaveType(l.getLeaveType());
        dto.setStartDate(l.getStartDate());
        dto.setEndDate(l.getEndDate());
        dto.setDaysCount(l.getDaysCount());
        dto.setReason(l.getReason());
        dto.setStatus(l.getStatus());
        dto.setManagerComments(l.getManagerComments());
        dto.setAppliedAt(l.getAppliedAt());
        dto.setReviewedAt(l.getReviewedAt());
        return dto;
    }
}
