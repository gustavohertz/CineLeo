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
public class LogEventDTO {

    private String servico;
    private String nivel;
    private String mensagem;
    private LocalDateTime timestamp;
}
