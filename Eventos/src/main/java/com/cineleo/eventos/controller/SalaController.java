package com.cineleo.eventos.controller;

import com.cineleo.eventos.dto.SalaRequestDTO;
import com.cineleo.eventos.dto.SalaResponseDTO;
import com.cineleo.eventos.service.SalaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;

    @GetMapping
    public ResponseEntity<List<SalaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(salaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<SalaResponseDTO> criar(@RequestBody @Valid SalaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid SalaRequestDTO dto) {
        return ResponseEntity.ok(salaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        salaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
