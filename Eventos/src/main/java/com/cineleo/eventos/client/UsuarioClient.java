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

    public UsuarioDTO buscarPorId(Long id) {
        try {
            return restTemplate.getForObject(usuariosUrl + "/usuarios/" + id, UsuarioDTO.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Usuário não encontrado com id: " + id);
        } catch (Exception ex) {
            throw new BusinessException("Serviço de usuários indisponível");
        }
    }

    @Data
    @Builder
    public static class UsuarioDTO {
        private Long id;
        private String nome;
        private String email;
        private String cpf;
        private String status;
    }
}
