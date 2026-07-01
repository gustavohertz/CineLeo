package com.cinelo.app.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReservaResponseDTO {
    private Long id;
    private Long sessaoId;
    private Long usuarioId;
    private String filmeNome;
    private LocalDateTime dataHoraSessao;
    private String salaNome;
    private String nomeCliente;
    private String emailCliente;
    private String cpfCliente;
    private Integer quantidadeIngressos;
    private BigDecimal valorTotal;
    private String status;
    private LocalDateTime criadoEm;
    private String codigoConfirmacao;
}