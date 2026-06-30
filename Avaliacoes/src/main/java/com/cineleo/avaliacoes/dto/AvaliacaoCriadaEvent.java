package com.cineleo.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoCriadaEvent {

    private String avaliacaoId;
    private String filmeId;
    private String usuarioId;
    private Double nota;
    private LocalDateTime criadaEm;
}
