package com.cineleo.eventos.client;

import com.cineleo.eventos.exception.BusinessException;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UsuarioClient {

    private static final Logger log = LoggerFactory.getLogger(UsuarioClient.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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

    // Com retry: busca por id é idempotente (GET). O 404 é ignorado (não é falha de
    // disponibilidade — ver ignore-exceptions na config), então não abre o circuito.
    @CircuitBreaker(name = "usuarios", fallbackMethod = "buscarPorIdFallback")
    @Retry(name = "usuarios")
    public UsuarioDTO buscarPorId(Long id) {
        try {
            return restTemplate.getForObject(usuariosUrl + "/usuarios/" + id, UsuarioDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        }
    }

    @SuppressWarnings("unused")
    private UsuarioDTO buscarPorIdFallback(Long id, Throwable t) {
        if (t instanceof ResourceNotFoundException rnfe) {
            throw rnfe; // repassa o 404 sem mascarar como indisponibilidade
        }
        log.warn("Fallback buscarPorId (serviço de usuários indisponível): {}", t.getMessage());
        throw new BusinessException("Serviço de usuários indisponível no momento. Tente novamente.");
    }

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
        } catch (Exception parseEx) {
            log.warn("Falha ao interpretar corpo de erro do serviço de usuários: {}", parseEx.getMessage());
        }
        return new BusinessException("Erro na requisição: " + ex.getMessage());
    }
}