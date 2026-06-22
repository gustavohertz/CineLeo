package com.cineleo.observabilidade.controller;

import com.cineleo.observabilidade.dto.DashboardDTO;
import com.cineleo.observabilidade.dto.ServicoStatusDTO;
import com.cineleo.observabilidade.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/observabilidade")
@RequiredArgsConstructor
public class DashboardController {

    private final HealthCheckService healthCheckService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> dashboard() {
        return ResponseEntity.ok(healthCheckService.getDashboard());
    }

    @GetMapping("/status")
    public ResponseEntity<List<ServicoStatusDTO>> status() {
        return ResponseEntity.ok(healthCheckService.getUltimoStatus());
    }
}
