package com.cinelo.app.dto;

import lombok.Data;

@Data
public class PagamentoReservaRequestDTO {
    private CartaoDTO cartao;

    @Data
    public static class CartaoDTO {
        private String numero;
        private String nomeTitular;
        private String mesExpiracao;
        private String anoExpiracao;
        private String cvv;
    }
}
