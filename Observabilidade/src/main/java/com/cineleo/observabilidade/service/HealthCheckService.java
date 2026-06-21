package com.cineleo.observabilidade.service;

import com.cineleo.observabilidade.dto.DashboardDTO;
import com.cineleo.observabilidade.dto.ServicoStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthCheckService {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    @Value("${observabilidade.servicos}")
    private String[] servicosMonitorados;

    private final List<ServicoStatusDTO> ultimoStatus = new CopyOnWriteArrayList<>();

    @Scheduled(fixedDelayString = "${observabilidade.check-interval}")
    public void verificarServicos() {
        log.info("Iniciando verificacao de saude dos servicos...");
        List<ServicoStatusDTO> resultados = new ArrayList<>();

        for (String nomeServico : servicosMonitorados) {
            ServicoStatusDTO status = verificarServico(nomeServico);
            resultados.add(status);

            if ("DOWN".equals(status.getStatus())) {
                log.warn("ALERTA: Servico {} esta OFFLINE!", nomeServico);
            }
        }

        ultimoStatus.clear();
        ultimoStatus.addAll(resultados);
        log.info("Verificacao concluida. {}/{} servicos online.",
                resultados.stream().filter(s -> "UP".equals(s.getStatus())).count(),
                resultados.size());
    }

    private ServicoStatusDTO verificarServico(String nomeServico) {
        List<ServiceInstance> instancias = discoveryClient.getInstances(nomeServico);

        if (instancias.isEmpty()) {
            return ServicoStatusDTO.builder()
                    .nome(nomeServico)
                    .status("DOWN")
                    .url("N/A")
                    .statusCode(0)
                    .tempoRespostaMs(-1)
                    .verificadoEm(LocalDateTime.now())
                    .build();
        }

        ServiceInstance instancia = instancias.get(0);
        String healthUrl = instancia.getUri() + "/actuator/health";

        long inicio = System.currentTimeMillis();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            long tempo = System.currentTimeMillis() - inicio;

            return ServicoStatusDTO.builder()
                    .nome(nomeServico)
                    .status("UP")
                    .url(instancia.getUri().toString())
                    .statusCode(response.getStatusCode().value())
                    .tempoRespostaMs(tempo)
                    .verificadoEm(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            long tempo = System.currentTimeMillis() - inicio;

            
            try {
                String customUrl = instancia.getUri() + "/health-check";
                ResponseEntity<String> response = restTemplate.getForEntity(customUrl, String.class);
                return ServicoStatusDTO.builder()
                        .nome(nomeServico)
                        .status("UP")
                        .url(instancia.getUri().toString())
                        .statusCode(response.getStatusCode().value())
                        .tempoRespostaMs(System.currentTimeMillis() - inicio)
                        .verificadoEm(LocalDateTime.now())
                        .build();
            } catch (Exception ex) {
                return ServicoStatusDTO.builder()
                        .nome(nomeServico)
                        .status("DOWN")
                        .url(instancia.getUri().toString())
                        .statusCode(0)
                        .tempoRespostaMs(tempo)
                        .verificadoEm(LocalDateTime.now())
                        .build();
            }
        }
    }

    public List<ServicoStatusDTO> getUltimoStatus() {
        if (ultimoStatus.isEmpty()) {
            verificarServicos();
        }
        return new ArrayList<>(ultimoStatus);
    }

    public DashboardDTO getDashboard() {
        List<ServicoStatusDTO> status = getUltimoStatus();
        long online = status.stream().filter(s -> "UP".equals(s.getStatus())).count();

        return DashboardDTO.builder()
                .totalServicos(status.size())
                .servicosOnline((int) online)
                .servicosOffline(status.size() - (int) online)
                .ultimaVerificacao(LocalDateTime.now())
                .servicos(status)
                .build();
    }
}
