package com.cineleo.recomendacao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Contrato do evento publicado pelo Avaliacoes ao criar uma avaliação.
// ignoreUnknown: tolera campos extras que o produtor envie (titulo, tags, etc.).
@JsonIgnoreProperties(ignoreUnknown = true)
public record AvaliacaoCriadaEvent(
        String avaliacaoId,
        String filmeId,
        String usuarioId,
        Double nota
) {
}
