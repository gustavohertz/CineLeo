package com.cinelo.app.client;

import com.cinelo.app.dto.RecomendacaoDTO;
import com.cinelo.app.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecomendacaoClient {

    private final RestTemplate restTemplate;

    @Value("${gateway.url}")
    private String gatewayUrl;

    private static final String RECOMENDACOES_PATH = "/api/recomendacoes";

    public List<RecomendacaoDTO> topRecomendados(int limite) {
        try {
            String url = gatewayUrl + RECOMENDACOES_PATH + "?limite=" + limite;
            ResponseEntity<List<RecomendacaoDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            throw new BusinessException("Falha ao buscar recomendações: " + e.getMessage());
        }
    }
}
