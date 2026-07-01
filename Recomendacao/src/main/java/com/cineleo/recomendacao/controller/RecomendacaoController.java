package com.cineleo.recomendacao.controller;

import com.cineleo.recomendacao.dto.RecomendacaoDTO;
import com.cineleo.recomendacao.service.RecomendacaoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recomendacoes")
public class RecomendacaoController {

    private final RecomendacaoService service;

    public RecomendacaoController(RecomendacaoService service) {
        this.service = service;
    }

    // Top filmes por média de nota.
    @GetMapping
    public List<RecomendacaoDTO> top(
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(defaultValue = "1") long minAvaliacoes) {
        return service.topFilmes(limite, minAvaliacoes);
    }

    // Recomendações para um usuário, excluindo filmes que ele já avaliou.
    @GetMapping("/usuario/{usuarioId}")
    public List<RecomendacaoDTO> paraUsuario(
            @PathVariable String usuarioId,
            @RequestParam(defaultValue = "10") int limite,
            @RequestParam(defaultValue = "1") long minAvaliacoes) {
        return service.paraUsuario(usuarioId, limite, minAvaliacoes);
    }
}
