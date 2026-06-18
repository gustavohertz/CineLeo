package com.leocine.controller;

import com.leocine.dto.ConsumeResponseDTO;
import com.leocine.dto.NotificationRequestDTO;
import com.leocine.dto.NotificationResponseDTO;
import com.leocine.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/consume")
    public ResponseEntity<ConsumeResponseDTO> consumeNotification(
            @Valid @RequestBody NotificationRequestDTO request) {
        NotificationResponseDTO response = notificationService.consumeNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ConsumeResponseDTO(response.getId(), "ok"));
    }

    @PostMapping("/send-email/{id}")
    public ResponseEntity<String> sendEmailById(@PathVariable String id) {
        notificationService.sendEmailById(id);
        return ResponseEntity.ok("Email sent successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable String id) {
        NotificationResponseDTO response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }
}