package com.cineleo.eventos.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class JwtUtil {

    private JwtUtil() {}

    public static String getSubject(String token) {
        return (String) extractPayload(token).get("sub");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractPayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Token JWT inválido");
        }
        byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
        String json = new String(decoded, StandardCharsets.UTF_8);
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao extrair payload do JWT", e);
        }
    }
}