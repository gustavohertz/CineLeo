package com.cineleo.avaliacoes.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class AvaliacaoRequestDTO {

    @NotBlank(message = "filmeId é obrigatório")
    private String filmeId;

    @NotBlank(message = "sessaoId é obrigatório")
    private String sessaoId;

    @NotBlank(message = "reservaId é obrigatório")
    private String reservaId;

    @NotNull(message = "nota é obrigatória")
    @DecimalMin(value = "1.0", message = "Nota mínima é 1.0")
    @DecimalMax(value = "5.0", message = "Nota máxima é 5.0")
    private Double nota;

    @NotBlank(message = "título é obrigatório")
    @Size(max = 100, message = "Título deve ter no máximo 100 caracteres")
    private String titulo;

    @Size(max = 1000, message = "Comentário deve ter no máximo 1000 caracteres")
    private String comentario;

    private List<String> tags;
}
