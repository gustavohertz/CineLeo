package com.cineleo.eventos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReservaRequestDTO {

    @NotNull(message = "ID da sessão é obrigatório")
    private Long sessaoId;

    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;

    @NotNull(message = "Quantidade de ingressos é obrigatória")
    @Min(value = 1, message = "Mínimo de 1 ingresso por reserva")
    @Max(value = 10, message = "Máximo de 10 ingressos por reserva")
    private Integer quantidadeIngressos;
}
