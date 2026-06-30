package com.cineleo.avaliacoes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoRemovidaEvent {

    private String avaliacaoId;
    private String filmeId;
    private String usuarioId;
}
