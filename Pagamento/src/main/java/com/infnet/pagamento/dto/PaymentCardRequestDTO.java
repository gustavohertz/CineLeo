package com.infnet.microservicesKafka.Services.paymentService.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentCardRequestDTO(
        @NotBlank String customerId,
        @NotBlank String billingType,   // CREDIT_CARD ou DEBIT_CARD
        @NotNull @Positive Double value,
        String description,
        @Valid CardDTO card
) {
    public record CardDTO(
            @NotBlank String number,
            @NotBlank String holderName,
            @NotBlank String expiryMonth,
            @NotBlank String expiryYear,
            @NotBlank String ccv
    ) {}
}