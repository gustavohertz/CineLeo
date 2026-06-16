package com.leocine.controller;

import com.leocine.dto.ConsumeResponseDTO;
import com.leocine.dto.NotificationRequestDTO;
import com.leocine.dto.NotificationResponseDTO;
import com.leocine.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/consume")
    public ResponseEntity<ConsumeResponseDTO> consumeNotification(@RequestBody NotificationRequestDTO notificationRequest) {
        try {
            NotificationResponseDTO response = notificationService.consumeNotification(notificationRequest);
            return ResponseEntity.ok(new ConsumeResponseDTO(response.getId(), "ok"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ConsumeResponseDTO(null, "error"));
        }
    }

    @PostMapping("/send-email/{id}")
    public ResponseEntity<String> sendEmailById(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.sendEmailById(id));
    }


    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getNotificationById(@PathVariable String id) {
        NotificationResponseDTO response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }
}

