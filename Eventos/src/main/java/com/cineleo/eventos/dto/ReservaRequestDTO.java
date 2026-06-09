package com.cineleo.eventos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReservaRequestDTO {

    @NotNull(message = "ID da sessão é obrigatório")
    private Long sessaoId;

    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nomeCliente;

    @NotBlank(message = "E-mail do cliente é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
    private String emailCliente;

    @NotBlank(message = "CPF do cliente é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos numéricos")
    private String cpfCliente;

    @NotNull(message = "Quantidade de ingressos é obrigatória")
    @Min(value = 1, message = "Mínimo de 1 ingresso por reserva")
    @Max(value = 10, message = "Máximo de 10 ingressos por reserva")
    private Integer quantidadeIngressos;
}
