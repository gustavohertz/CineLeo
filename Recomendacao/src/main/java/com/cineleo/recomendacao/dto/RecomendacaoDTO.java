package com.cineleo.recomendacao.dto;

public record RecomendacaoDTO(
        String filmeId,
        Double mediaNota,
        Long totalAvaliacoes
) {
}
