package com.cineleo.eventos.dto;

import com.cineleo.eventos.entity.Reserva;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReservaResponseDTO {

    private Long id;
    private Long sessaoId;
    private String filmeNome;
    private LocalDateTime dataHoraSessao;
    private String salaNome;
    private String nomeCliente;
    private String emailCliente;
    private String cpfCliente;
    private Integer quantidadeIngressos;
    private BigDecimal valorTotal;
    private Reserva.StatusReserva status;
    private LocalDateTime criadoEm;
    private String codigoConfirmacao;

    public static ReservaResponseDTO from(Reserva reserva) {
        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .sessaoId(reserva.getSessao().getId())
                .filmeNome(reserva.getSessao().getFilme().getNome())
                .dataHoraSessao(reserva.getSessao().getDataHoraInicio())
                .salaNome(reserva.getSessao().getSala().getNome())
                .nomeCliente(reserva.getNomeCliente())
                .emailCliente(reserva.getEmailCliente())
                .cpfCliente(reserva.getCpfCliente())
                .quantidadeIngressos(reserva.getQuantidadeIngressos())
                .valorTotal(reserva.getValorTotal())
                .status(reserva.getStatus())
                .criadoEm(reserva.getCriadoEm())
                .codigoConfirmacao(reserva.getCodigoConfirmacao())
                .build();
    }
}
