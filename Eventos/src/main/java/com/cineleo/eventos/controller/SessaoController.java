package com.cineleo.eventos.controller;

import com.cineleo.eventos.dto.SessaoRequestDTO;
import com.cineleo.eventos.dto.SessaoResponseDTO;
import com.cineleo.eventos.service.SessaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessoes")
@RequiredArgsConstructor
public class SessaoController {

    private final SessaoService sessaoService;

    @GetMapping
    public ResponseEntity<List<SessaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(sessaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessaoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sessaoService.buscarPorId(id));
    }

    @GetMapping("/filme/{filmeId}")
    public ResponseEntity<List<SessaoResponseDTO>> listarPorFilme(@PathVariable Long filmeId) {
        return ResponseEntity.ok(sessaoService.listarPorFilme(filmeId));
    }

    @PostMapping
    public ResponseEntity<SessaoResponseDTO> criar(@RequestBody @Valid SessaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoService.criar(dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<SessaoResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(sessaoService.cancelar(id));
    }
}
