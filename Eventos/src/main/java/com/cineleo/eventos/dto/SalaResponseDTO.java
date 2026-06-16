package com.cineleo.eventos.dto;

import com.cineleo.eventos.entity.Sala;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalaResponseDTO {

    private Long id;
    private String nome;
    private Integer capacidade;
    private Sala.TipoSala tipo;

    public static SalaResponseDTO from(Sala sala) {
        return SalaResponseDTO.builder()
                .id(sala.getId())
                .nome(sala.getNome())
                .capacidade(sala.getCapacidade())
                .tipo(sala.getTipo())
                .build();
    }
}
