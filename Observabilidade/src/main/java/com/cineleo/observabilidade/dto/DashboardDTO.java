package com.cineleo.observabilidade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {

    private int totalServicos;
    private int servicosOnline;
    private int servicosOffline;
    private LocalDateTime ultimaVerificacao;
    private List<ServicoStatusDTO> servicos;
}
