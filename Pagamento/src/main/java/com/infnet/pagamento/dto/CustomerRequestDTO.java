package com.infnet.microservicesKafka.Services.paymentService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequestDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String cpf
) {}