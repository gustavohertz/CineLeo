package com.cineleo.eventos.dto;

import com.cineleo.eventos.entity.Filme;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmeResponseDTO {

    private Long id;
    private String nome;
    private Filme.ClassificacaoIndicativa classificacaoIndicativa;
    private EnderecoDTO endereco;
    private Filme.StatusFilme status;

    public static FilmeResponseDTO from(Filme filme) {
        return FilmeResponseDTO.builder()
                .id(filme.getId())
                .nome(filme.getNome())
                .classificacaoIndicativa(filme.getClassificacaoIndicativa())
                .endereco(EnderecoDTO.from(filme.getEndereco()))
                .status(filme.getStatus())
                .build();
    }
}
