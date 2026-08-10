package com.paycore.controller;

import com.paycore.dto.ApiResponse;
import com.paycore.dto.LeaveRequestDto;
import com.paycore.entity.LeaveRequest.LeaveStatus;
import com.paycore.entity.User;
import com.paycore.repository.UserRepository;
import com.paycore.service.EmployeeService;
import com.paycore.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LeaveRequestDto>>> getAllLeaveRequests() {
        List<LeaveRequestDto> leaves = leaveService.getAllLeaveRequests();
        return ResponseEntity.ok(new ApiResponse<>(true, "Leave requests retrieved", leaves));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<LeaveRequestDto>>> getMyLeaveRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long employeeId = employeeService.getEmployeeByUserId(user.getId()).getId();
        List<LeaveRequestDto> leaves = leaveService.getEmployeeLeaveRequests(employeeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "My leave requests retrieved", leaves));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<LeaveRequestDto>> applyForLeave(@Valid @RequestBody LeaveRequestDto dto) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Long employeeId = employeeService.getEmployeeByUserId(user.getId()).getId();
            LeaveRequestDto applied = leaveService.applyForLeave(employeeId, dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Leave request submitted successfully", applied));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LeaveRequestDto>> reviewLeaveRequest(
            @PathVariable Long id,
            @RequestParam LeaveStatus status,
            @RequestParam(required = false) String comments) {
        LeaveRequestDto reviewed = leaveService.reviewLeaveRequest(id, status, comments);
        return ResponseEntity.ok(new ApiResponse<>(true, "Leave request " + status.name().toLowerCase() + " successfully", reviewed));
    }
}
