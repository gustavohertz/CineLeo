package com.cineleo.avaliacoes.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class AvaliacaoListaDTO {

    private String filmeId;
    private Long totalAvaliacoes;
    private Double mediaGeral;
    private List<AvaliacaoResponseDTO> avaliacoes;
}
