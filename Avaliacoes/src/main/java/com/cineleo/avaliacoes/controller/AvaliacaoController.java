package com.cineleo.avaliacoes.controller;

import com.cineleo.avaliacoes.dto.AvaliacaoRequestDTO;
import com.cineleo.avaliacoes.dto.AvaliacaoResponseDTO;
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

        String usuarioId   = jwt.getSubject();
        String usuarioNome = jwt.getClaimAsString("name");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(avaliacaoService.criar(dto, usuarioId, usuarioNome));
    }

    @GetMapping("/filme/{filmeId}")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorFilme(@PathVariable String filmeId) {
        return ResponseEntity.ok(avaliacaoService.listarPorFilme(filmeId));
    }

    @GetMapping("/minha")
    public ResponseEntity<List<AvaliacaoResponseDTO>> minhasAvaliacoes(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(avaliacaoService.minhasAvaliacoes(jwt.getSubject()));
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
}
