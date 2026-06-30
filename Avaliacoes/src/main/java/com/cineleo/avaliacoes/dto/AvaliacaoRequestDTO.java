package com.cineleo.avaliacoes.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AvaliacaoRequestDTO {

    @NotBlank(message = "filmeId é obrigatório")
    private String filmeId;

    @NotNull(message = "nota é obrigatória")
    @DecimalMin(value = "1.0", message = "Nota mínima é 1.0")
    @DecimalMax(value = "5.0", message = "Nota máxima é 5.0")
    private Double nota;

    @Size(max = 500, message = "Comentário deve ter no máximo 500 caracteres")
    private String comentario;
}
