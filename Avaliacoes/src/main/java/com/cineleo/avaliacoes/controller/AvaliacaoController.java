package com.cineleo.avaliacoes.controller;

import com.cineleo.avaliacoes.dto.*;
import com.cineleo.avaliacoes.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> criar(
            @RequestBody @Valid AvaliacaoRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        String usuarioId = jwt.getSubject();
        String usuarioNome = jwt.getClaimAsString("name");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(avaliacaoService.criar(dto, usuarioId, usuarioNome));
    }

    @GetMapping("/filme/{filmeId}")
    public ResponseEntity<AvaliacaoListaDTO> listarPorFilme(@PathVariable String filmeId) {
        return ResponseEntity.ok(avaliacaoService.listarPorFilme(filmeId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorUsuario(@PathVariable String usuarioId) {
        return ResponseEntity.ok(avaliacaoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/filme/{filmeId}/resumo")
    public ResponseEntity<AvaliacaoResumoDTO> buscarResumo(@PathVariable String filmeId) {
        return ResponseEntity.ok(avaliacaoService.buscarResumo(filmeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoResponseDTO> editar(
            @PathVariable String id,
            @RequestBody @Valid AvaliacaoRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(avaliacaoService.editar(id, dto, jwt.getSubject()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

        avaliacaoService.remover(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/curtir")
    public ResponseEntity<AvaliacaoResponseDTO> curtir(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(avaliacaoService.curtir(id, jwt.getSubject()));
    }

    @PostMapping("/{id}/denunciar")
    public ResponseEntity<Void> denunciar(
            @PathVariable String id,
            @AuthenticationPrincipal Jwt jwt) {

        avaliacaoService.denunciar(id, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
