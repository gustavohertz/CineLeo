package com.infnet.microservicesKafka.dto;

public record PagamentoEvento(
        String idReserva,
        String idUsuario,
        String emailUsuario,
        String cpfUser,
        String status,
        String mensagemErro,
        String idPagamento
) {}
