package com.cineleo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentCardRequestDTO(
        @NotBlank String customerId,
        @NotBlank String billingType,
        @NotNull @Positive Double value,
        String description
) {}