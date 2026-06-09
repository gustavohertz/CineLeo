package com.cineleo.eventos.dto;

import com.cineleo.eventos.entity.Filme;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FilmeRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String nome;

    @NotNull(message = "Classificação indicativa é obrigatória")
    private Filme.ClassificacaoIndicativa classificacaoIndicativa;

    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private EnderecoDTO endereco;
}
