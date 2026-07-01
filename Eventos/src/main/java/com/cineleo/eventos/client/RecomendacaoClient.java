package com.cineleo.eventos.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecomendacaoClient {

    private static final Logger log = LoggerFactory.getLogger(RecomendacaoClient.class);

    private final RestTemplate restTemplate;

    @Value("${services.recomendacao.url}")
    private String recomendacaoUrl;

    public List<RecomendacaoDTO> topRecomendados(int limite) {
        try {
            RecomendacaoDTO[] resposta = restTemplate.getForObject(
                    recomendacaoUrl + "/recomendacoes?limite=" + limite, RecomendacaoDTO[].class);
            return resposta == null ? Collections.emptyList() : Arrays.asList(resposta);
        } catch (Exception ex) {
            log.warn("Serviço de recomendação indisponível: {}", ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Data
    public static class RecomendacaoDTO {
        private String filmeId;
        private Double mediaNota;
        private Long totalAvaliacoes;
    }
}
