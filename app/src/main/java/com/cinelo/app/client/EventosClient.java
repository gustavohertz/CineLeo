package com.cinelo.app.client;

import com.cinelo.app.dto.FilmeResponseDTO;
import com.cinelo.app.dto.PagamentoReservaRequestDTO;
import com.cinelo.app.dto.ReservaRequestDTO;
import com.cinelo.app.dto.ReservaResponseDTO;
import com.cinelo.app.dto.SessaoResponseDTO;
import com.cinelo.app.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventosClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gateway.url}")
    private String gatewayUrl;

    private static final String EVENTOS_PATH = "/api/eventos";
    private static final String FILMES_PATH = "/filmes";
    private static final String SESSOES_PATH = "/sessoes";
    private static final String RESERVAS_PATH = "/reservas";

    public List<FilmeResponseDTO> listarFilmesAtivos() {
        try {
            String url = gatewayUrl + EVENTOS_PATH + FILMES_PATH + "/ativos";
            ResponseEntity<List<FilmeResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Falha ao listar filmes: " + e.getMessage());
        }
    }

    public List<SessaoResponseDTO> listarSessoesPorFilme(Long filmeId) {
        try {
            String url = gatewayUrl + EVENTOS_PATH + SESSOES_PATH + "/filme/" + filmeId;
            ResponseEntity<List<SessaoResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Falha ao listar sessões: " + e.getMessage());
        }
    }

    public ReservaResponseDTO criarReserva(String token, ReservaRequestDTO request) {
        try {
            String url = gatewayUrl + EVENTOS_PATH + RESERVAS_PATH;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<ReservaRequestDTO> entity = new HttpEntity<>(request, headers);
            return restTemplate.postForObject(url, entity, ReservaResponseDTO.class);
        } catch (HttpClientErrorException ex) {
            throw parseError(ex);
        } catch (Exception e) {
            throw new BusinessException("Falha ao criar reserva: " + e.getMessage());
        }
    }

    public ReservaResponseDTO pagarReserva(String token, Long reservaId, PagamentoReservaRequestDTO request) {
        try {
            String url = gatewayUrl + EVENTOS_PATH + RESERVAS_PATH + "/" + reservaId + "/pagar";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<PagamentoReservaRequestDTO> entity = new HttpEntity<>(request, headers);
            return restTemplate.postForObject(url, entity, ReservaResponseDTO.class);
        } catch (HttpClientErrorException ex) {
            throw parseError(ex);
        } catch (Exception e) {
            throw new BusinessException("Falha ao pagar reserva: " + e.getMessage());
        }
    }

    public List<ReservaResponseDTO> listarReservasPorEmail(String token, String email) {
        try {
            String url = gatewayUrl + EVENTOS_PATH + RESERVAS_PATH + "/cliente?email=" + email;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<List<ReservaResponseDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Falha ao listar reservas: " + e.getMessage());
        }
    }

    public ReservaResponseDTO buscarReservaPorId(String token, Long reservaId) {
        try {
            String url = gatewayUrl + EVENTOS_PATH + RESERVAS_PATH + "/" + reservaId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<ReservaResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ReservaResponseDTO.class
            );
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Falha ao buscar reserva: " + e.getMessage());
        }
    }

    private BusinessException parseError(HttpClientErrorException ex) {
        try {
            String body = ex.getResponseBodyAsString();
            return new BusinessException("Erro: " + body);
        } catch (Exception e) {
            return new BusinessException("Erro na requisição: " + ex.getMessage());
        }
    }
}