package com.paycore.controller;

import com.paycore.dto.ApiResponse;
import com.paycore.dto.NotificationDto;
import com.paycore.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getMyNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<NotificationDto> notifications = notificationService.getUserNotifications(auth.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Notifications retrieved", notifications));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as read"));
    }
}
