package com.cineleo.usuarios.dto;

public record LoginReponseDTO(String accessToken, String tokenType, long expiresIn) {}