package com.cinelo.app.dto;

public record LoginResponseDTO(String accessToken, String tokenType, long expiresIn) {}