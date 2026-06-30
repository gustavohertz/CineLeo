package com.cineleo.avaliacoes.dto;

import com.cineleo.avaliacoes.entity.Avaliacao;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AvaliacaoResponseDTO {

    private String id;
    private String filmeId;
    private String usuarioId;
    private String usuarioNome;
    private Double nota;
    private String comentario;
    private LocalDateTime criadaEm;
    private LocalDateTime atualizadaEm;

    public static AvaliacaoResponseDTO from(Avaliacao a) {
        return AvaliacaoResponseDTO.builder()
                .id(a.getId())
                .filmeId(a.getFilmeId())
                .usuarioId(a.getUsuarioId())
                .usuarioNome(a.getUsuarioNome())
                .nota(a.getNota())
                .comentario(a.getComentario())
                .criadaEm(a.getCriadaEm())
                .atualizadaEm(a.getAtualizadaEm())
                .build();
    }
}
