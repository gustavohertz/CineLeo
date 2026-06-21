package com.cineleo.eventos.client;

import com.cineleo.eventos.exception.BusinessException;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class UsuarioClient {

    private final RestTemplate restTemplate;

    @Value("${services.usuarios.url}")
    private String usuariosUrl;

    // Login real
    public LoginResponse login(String email, String senha) {
        try {
            LoginRequest request = new LoginRequest(email, senha);
            LoginResponse response = restTemplate.postForObject(
                    usuariosUrl + "/usuarios/login", request, LoginResponse.class);
            if (response == null || response.getAccessToken() == null) {
                throw new BusinessException("Falha na autenticação: resposta inválida");
            }
            return response;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Serviço de usuários indisponível: " + ex.getMessage());
        }
    }

    // Cadastro real
    public UsuarioDTO criarUsuario(UsuarioRequest request) {
        try {
            UsuarioDTO response = restTemplate.postForObject(
                    usuariosUrl + "/usuarios/create", request, UsuarioDTO.class);
            if (response == null || response.getId() == null) {
                throw new BusinessException("Falha ao criar usuário: resposta inválida");
            }
            return response;
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
        private String status; // opcional, mantido para compatibilidade
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
}