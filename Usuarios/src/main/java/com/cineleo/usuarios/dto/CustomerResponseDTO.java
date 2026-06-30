package com.cineleo.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerResponseDTO {
    @JsonProperty("customerId")
    private String customerId;
}