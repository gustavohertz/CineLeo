package com.cineleo.eventos.controller;

import com.cineleo.eventos.dto.ReservaRequestDTO;
import com.cineleo.eventos.dto.ReservaResponseDTO;
import com.cineleo.eventos.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ReservaResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(reservaService.buscarPorCodigo(codigo));
    }

    @GetMapping("/sessao/{sessaoId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorSessao(@PathVariable Long sessaoId) {
        return ResponseEntity.ok(reservaService.listarPorSessao(sessaoId));
    }

    @GetMapping("/cliente")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorEmail(@RequestParam String email) {
        return ResponseEntity.ok(reservaService.listarPorEmail(email));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criar(@RequestBody @Valid ReservaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.criar(dto));
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponseDTO> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.confirmar(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }
}
