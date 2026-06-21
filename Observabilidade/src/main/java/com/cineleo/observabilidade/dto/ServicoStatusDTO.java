package com.cineleo.observabilidade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServicoStatusDTO {

    private String nome;
    private String status;
    private String url;
    private int statusCode;
    private long tempoRespostaMs;
    private LocalDateTime verificadoEm;
}
