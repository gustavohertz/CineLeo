package com.cineleo.eventos.controller;

import com.cineleo.eventos.dto.FilmeRequestDTO;
import com.cineleo.eventos.dto.FilmeResponseDTO;
import com.cineleo.eventos.service.FilmeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filmes")
@RequiredArgsConstructor
public class FilmeController {

    private final FilmeService filmeService;

    @GetMapping
    public ResponseEntity<List<FilmeResponseDTO>> listarTodos() {
        return ResponseEntity.ok(filmeService.listarTodos());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<FilmeResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(filmeService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(filmeService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FilmeResponseDTO> criar(@RequestBody @Valid FilmeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(filmeService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid FilmeRequestDTO dto) {
        return ResponseEntity.ok(filmeService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        filmeService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
