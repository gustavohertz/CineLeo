package com.cineleo.eventos.client;

import com.cineleo.eventos.exception.BusinessException;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UsuarioClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${services.usuarios.url}")
    private String usuariosUrl;

    public LoginResponse login(String email, String senha) {
        try {
            LoginRequest request = new LoginRequest(email, senha);
            LoginResponse response = restTemplate.postForObject(
                    usuariosUrl + "/usuarios/login", request, LoginResponse.class);
            if (response == null || response.getAccessToken() == null) {
                throw new BusinessException("Falha na autenticação: resposta inválida");
            }
            return response;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 401) {
                throw new BusinessException("E-mail ou senha inválidos.");
            }
            throw parseError(ex);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Serviço de usuários indisponível");
        }
    }

    public UsuarioDTO criarUsuario(UsuarioRequest request) {
        try {
            UsuarioDTO response = restTemplate.postForObject(
                    usuariosUrl + "/usuarios/create", request, UsuarioDTO.class);
            if (response == null || response.getId() == null) {
                throw new BusinessException("Falha ao criar usuário: resposta inválida");
            }
            return response;
        } catch (HttpClientErrorException ex) {
            throw parseError(ex);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Serviço de usuários indisponível: " + ex.getMessage());
        }
    }

    public UsuarioDTO buscarPorId(Long id) {
        try {
            return restTemplate.getForObject(usuariosUrl + "/usuarios/" + id, UsuarioDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        } catch (Exception ex) {
            throw new BusinessException("Serviço de usuários indisponível");
        }
    }

    // --- DTOs ---

    @Data
    @Builder
    public static class UsuarioDTO {
        private Long id;
        private String nome;
        private Integer idade;
        private String email;
        private String cpf;
        private boolean ativo;
        private String status;
    }

    @Data
    public static class UsuarioRequest {
        private String nome;
        private Integer idade;
        private String email;
        private String cpf;
        private String senha;
    }

    @Data
    private static class LoginRequest {
        private final String email;
        private final String senha;
    }

    @Data
    public static class LoginResponse {
        private String accessToken;
        private String tokenType;
        private long expiresIn;
    }

    private BusinessException parseError(HttpClientErrorException ex) {
        try {
            String body = ex.getResponseBodyAsString();
            Map<String, Object> errorMap = objectMapper.readValue(body, Map.class);
            if (errorMap.containsKey("erros") && errorMap.get("erros") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> fieldErrors = (Map<String, String>) errorMap.get("erros");
                StringBuilder sb = new StringBuilder();
                fieldErrors.forEach((field, msg) -> sb.append(field).append(": ").append(msg).append("\n"));
                return new BusinessException(sb.toString().trim());
            }
            if (errorMap.containsKey("mensagem")) {
                return new BusinessException(errorMap.get("mensagem").toString());
            }
        } catch (Exception ignored) {}
        return new BusinessException("Erro na requisição: " + ex.getMessage());
    }
}