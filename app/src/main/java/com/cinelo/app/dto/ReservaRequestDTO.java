package com.cinelo.app.dto;

import lombok.Data;

@Data
public class ReservaRequestDTO {
    private Long sessaoId;
    private Long usuarioId;
    private Integer quantidadeIngressos;
}