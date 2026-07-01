package com.cinelo.app.dto;

import lombok.Data;

@Data
public class RecomendacaoDTO {
    private String filmeId;
    private Double mediaNota;
    private Long totalAvaliacoes;
}
