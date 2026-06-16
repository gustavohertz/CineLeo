package com.cineleo.usuarios.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    private String nome;

    @NotNull(message = "Idade é obrigatória")
    @Min(value = 1, message = "Idade deve ser maior que 0")
    @Max(value = 120, message = "Idade inválida")
    private Integer idade;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos numéricos")
    private String cpf;
}
