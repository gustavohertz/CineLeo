package com.cineleo.eventos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagamentoReservaRequestDTO {

    @NotNull(message = "Dados do cartão são obrigatórios")
    @Valid
    private CartaoDTO cartao;

    @Data
    public static class CartaoDTO {

        @NotBlank(message = "Número do cartão é obrigatório")
        private String numero;

        @NotBlank(message = "Nome do titular é obrigatório")
        private String nomeTitular;

        @NotBlank(message = "Mês de expiração é obrigatório")
        private String mesExpiracao;

        @NotBlank(message = "Ano de expiração é obrigatório")
        private String anoExpiracao;

        @NotBlank(message = "CVV é obrigatório")
        private String cvv;
    }
}
