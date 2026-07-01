package com.cinelo.app.client;

import com.cinelo.app.dto.AvaliacaoRequestDTO;
import com.cinelo.app.dto.AvaliacaoResponseDTO;
import com.cinelo.app.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvaliacaoClient {

    private final RestTemplate restTemplate;

    @Value("${gateway.url}")
    private String gatewayUrl;

    private static final String AVALIACOES_PATH = "/api/avaliacoes";

    public AvaliacaoResponseDTO avaliar(String token, AvaliacaoRequestDTO request) {
        try {
            String url = gatewayUrl + AVALIACOES_PATH;
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<AvaliacaoRequestDTO> entity = new HttpEntity<>(request, headers);
            return restTemplate.postForObject(url, entity, AvaliacaoResponseDTO.class);
        } catch (HttpClientErrorException ex) {
            throw new BusinessException("Erro ao avaliar: " + ex.getResponseBodyAsString());
        } catch (Exception e) {
            throw new BusinessException("Falha ao avaliar: " + e.getMessage());
        }
    }
}
