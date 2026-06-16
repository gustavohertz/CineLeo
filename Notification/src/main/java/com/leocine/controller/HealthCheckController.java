package com.leocine.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health-check")
public class HealthCheckController {

    @Value("${server.port}")
    private int serverPort;

    @GetMapping
    public ResponseEntity<Map<String, String>> check() {
        Map<String, String> status = new HashMap<>();

        if (serverPort <= 0) {
            status.put("success", "error");
            status.put("message", "Invalid server port: " + serverPort);
            return ResponseEntity.status(503).body(status);
        }

        status.put("success", "ok");
        return ResponseEntity.ok(status);
    }
}