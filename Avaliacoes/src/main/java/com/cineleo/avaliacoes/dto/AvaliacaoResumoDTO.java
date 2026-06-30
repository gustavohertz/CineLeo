package com.cineleo.avaliacoes.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AvaliacaoResumoDTO {

    private String filmeId;
    private Long totalAvaliacoes;
    private Double mediaGeral;

    private Map<String, Long> distribuicaoNotas;

    private List<String> tagsMaisFrequentes;
    private LocalDateTime atualizadoEm;
}
