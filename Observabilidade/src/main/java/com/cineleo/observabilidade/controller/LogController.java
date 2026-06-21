package com.cineleo.observabilidade.controller;

import com.cineleo.observabilidade.dto.LogEventDTO;
import com.cineleo.observabilidade.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/observabilidade/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public ResponseEntity<Void> registrar(@RequestBody LogEventDTO logEvent) {
        logService.registrar(logEvent);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<LogEventDTO>> listarTodos() {
        return ResponseEntity.ok(logService.listarTodos());
    }

    @GetMapping("/servico/{servico}")
    public ResponseEntity<List<LogEventDTO>> listarPorServico(@PathVariable String servico) {
        return ResponseEntity.ok(logService.listarPorServico(servico));
    }

    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<LogEventDTO>> listarPorNivel(@PathVariable String nivel) {
        return ResponseEntity.ok(logService.listarPorNivel(nivel));
    }
}
