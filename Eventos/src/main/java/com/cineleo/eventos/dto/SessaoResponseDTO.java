package com.cineleo.eventos.dto;

import com.cineleo.eventos.entity.Sessao;
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
    private Sessao.StatusSessao status;
    private Integer assentosDisponiveis;

    public static SessaoResponseDTO from(Sessao sessao) {
        return SessaoResponseDTO.builder()
                .id(sessao.getId())
                .filmeId(sessao.getFilme().getId())
                .filmeNome(sessao.getFilme().getNome())
                .salaId(sessao.getSala().getId())
                .salaNome(sessao.getSala().getNome())
                .dataHoraInicio(sessao.getDataHoraInicio())
                .dataHoraFim(sessao.getDataHoraFim())
                .preco(sessao.getPreco())
                .status(sessao.getStatus())
                .assentosDisponiveis(sessao.getAssentosDisponiveis())
                .build();
    }
}
