package com.cinelo.app.client;

import com.cinelo.app.dto.LoginRequestDTO;
import com.cinelo.app.dto.LoginResponseDTO;
import com.cinelo.app.dto.UsuarioDTO;
import com.cinelo.app.dto.UsuarioRequestDTO;
import com.cinelo.app.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gateway.url}")
    private String gatewayUrl;

    private static final String USUARIOS_PATH = "/api/usuarios";

    public LoginResponseDTO login(String email, String senha) {
        try {
            LoginRequestDTO request = new LoginRequestDTO(email, senha);
            String url = gatewayUrl + USUARIOS_PATH + "/login";
            LoginResponseDTO response = restTemplate.postForObject(url, request, LoginResponseDTO.class);
            if (response == null || response.accessToken() == null) {
                throw new BusinessException("Falha na autenticação: resposta inválida");
            }
            return response;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new BusinessException("E-mail ou senha inválidos.");
            }
            throw parseError(ex);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Serviço de usuários indisponível");
        }
    }

    public UsuarioDTO criarUsuario(UsuarioRequestDTO request) {
        try {
            String url = gatewayUrl + USUARIOS_PATH + "/create";
            UsuarioDTO response = restTemplate.postForObject(url, request, UsuarioDTO.class);
            if (response == null || response.getId() == null) {
                throw new BusinessException("Falha ao criar usuário: resposta inválida");
            }
            return response;
        } catch (HttpClientErrorException ex) {
            throw parseError(ex);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Serviço de usuários indisponível: " + e.getMessage());
        }
    }

    public UsuarioDTO buscarPorId(Long id) {
        try {
            String url = gatewayUrl + USUARIOS_PATH + "/" + id;
            return restTemplate.getForObject(url, UsuarioDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new BusinessException("Usuário não encontrado com id: " + id);
        } catch (Exception e) {
            throw new BusinessException("Serviço de usuários indisponível");
        }
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
