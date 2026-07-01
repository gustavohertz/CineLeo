package com.cinelo.app.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SessaoResponseDTO {
    private Long id;
    private Long filmeId;
    private String filmeNome;
    private Long salaId;
    private String salaNome;
    private LocalDateTime dataHoraInicio;
    private LocalDateTime dataHoraFim;
    private BigDecimal preco;
    private String status;
    private Integer assentosDisponiveis;
}