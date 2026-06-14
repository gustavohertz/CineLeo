package com.cineleo.controller;

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

    @Value("${asaas.api.url}")
    private String asaasApiUrl;

    @Value("${asaas.api.key}")
    private String asaasApiKey;

    @GetMapping
    public ResponseEntity<Map<String, String>> check() {
        Map<String, String> status = new HashMap<>();

        // verifica se a porta é válida
        if (serverPort <= 0) {
            status.put("success", "error");
            status.put("message", "Invalid server port: " + serverPort);
            return ResponseEntity.status(503).body(status);
        }

        // verifica se as propriedades obrigatórias estão preenchidas
        if (isBlank(asaasApiUrl)) {
            status.put("success", "error");
            status.put("message", "Property 'asaas.api.url' is missing or empty");
            return ResponseEntity.status(503).body(status);
        }
        if (isBlank(asaasApiKey)) {
            status.put("success", "error");
            status.put("message", "Property 'asaas.api.key' is missing or empty");
            return ResponseEntity.status(503).body(status);
        }

        // se tudo ok
        status.put("success", "ok");
        return ResponseEntity.ok(status);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}