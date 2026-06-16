package com.cineleo.eventos.dto;

import com.cineleo.eventos.entity.Sala;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SalaRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
    private String nome;

    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser maior que 0")
    private Integer capacidade;

    @NotNull(message = "Tipo é obrigatório")
    private Sala.TipoSala tipo;
}
