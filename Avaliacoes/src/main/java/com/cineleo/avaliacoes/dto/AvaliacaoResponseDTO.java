package com.cineleo.avaliacoes.dto;

import com.cineleo.avaliacoes.entity.Avaliacao;
import com.cineleo.avaliacoes.enums.StatusAvaliacao;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AvaliacaoResponseDTO {

    private String id;
    private String filmeId;
    private String sessaoId;
    private String reservaId;
    private String usuarioId;
    private String usuarioNome;
    private Double nota;
    private String titulo;
    private String comentario;
    private List<String> tags;
    private Integer curtidas;
    private Integer denuncias;
    private StatusAvaliacao status;
    private LocalDateTime criadaEm;
    private LocalDateTime atualizadaEm;

    public static AvaliacaoResponseDTO from(Avaliacao avaliacao) {
        return AvaliacaoResponseDTO.builder()
                .id(avaliacao.getId())
                .filmeId(avaliacao.getFilmeId())
                .sessaoId(avaliacao.getSessaoId())
                .reservaId(avaliacao.getReservaId())
                .usuarioId(avaliacao.getUsuarioId())
                .usuarioNome(avaliacao.getUsuarioNome())
                .nota(avaliacao.getNota())
                .titulo(avaliacao.getTitulo())
                .comentario(avaliacao.getComentario())
                .tags(avaliacao.getTags())
                .curtidas(avaliacao.getCurtidas())
                .denuncias(avaliacao.getDenuncias())
                .status(avaliacao.getStatus())
                .criadaEm(avaliacao.getCriadaEm())
                .atualizadaEm(avaliacao.getAtualizadaEm())
                .build();
    }
}
