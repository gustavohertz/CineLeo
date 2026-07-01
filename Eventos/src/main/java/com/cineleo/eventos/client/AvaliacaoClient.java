package com.cineleo.eventos.client;

import com.cineleo.eventos.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class AvaliacaoClient {

    private final RestTemplate restTemplate;

    @Value("${services.avaliacoes.url}")
    private String avaliacoesUrl;

    public AvaliacaoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AvaliacaoResponse criarAvaliacao(AvaliacaoRequest request, String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");

            HttpEntity<AvaliacaoRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AvaliacaoResponse> response = restTemplate.exchange(
                    avaliacoesUrl + "/avaliacoes",
                    HttpMethod.POST,
                    entity,
                    AvaliacaoResponse.class
            );

            return response.getBody();

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 409) {
                throw new BusinessException("Você já avaliou este filme!");
            }
            if (ex.getStatusCode().value() == 400) {
                throw new BusinessException("Dados de avaliação inválidos. Verifique a nota (1.0 a 5.0).");
            }
            throw new BusinessException("Erro ao criar avaliação: " + ex.getMessage());
        } catch (Exception ex) {
            throw new BusinessException("Serviço de avaliações indisponível no momento.");
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvaliacaoRequest {
        private String filmeId;
        private Double nota;
        private String comentario;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvaliacaoResponse {
        private String id;
        private String filmeId;
        private Double nota;
        private String comentario;
    }
}
