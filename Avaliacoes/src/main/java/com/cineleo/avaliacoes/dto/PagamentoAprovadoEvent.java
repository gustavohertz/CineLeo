package com.cineleo.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoAprovadoEvent {

    private String reservaId;
    private String usuarioId;
    private String filmeId;
    private String sessaoId;
}
