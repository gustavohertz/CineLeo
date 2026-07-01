package com.cineleo.service;

import com.cineleo.dto.ConsumeResponseDTO;
import com.cineleo.dto.NotificationRequestDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationClientService {

    private static final Logger log = LoggerFactory.getLogger(NotificationClientService.class);

    private final RestTemplate loadBalancedRestTemplate;

    private static final String GATEWAY_SERVICE_NAME = "API-GATEWAY";
    private static final String NOTIFICATION_PATH = "/api/notificacoes";

    public NotificationClientService(RestTemplate loadBalancedRestTemplate) {
        this.loadBalancedRestTemplate = loadBalancedRestTemplate;
    }

    @Retry(name = "notification")
    @CircuitBreaker(name = "notification", fallbackMethod = "fallbackCreateAndSend")
    public void createAndSendEmail(String userID, String userEmail, String message, OffsetDateTime dateTime) {
        try {
            NotificationRequestDTO request = new NotificationRequestDTO(
                    userID,
                    userEmail,
                    message,
                    dateTime != null ? dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null);

            ConsumeResponseDTO response = loadBalancedRestTemplate.postForObject(
                    "lb://" + GATEWAY_SERVICE_NAME + NOTIFICATION_PATH + "/notification/consume",
                    request,
                    ConsumeResponseDTO.class);

            if (response == null || response.id() == null) {
                log.warn("Resposta inesperada ao criar notificação: {}", response);
                return;
            }

            String notificationId = response.id();
            log.info("Notificação criada com sucesso: id={}", notificationId);

            loadBalancedRestTemplate.postForObject(
                    "lb://" + GATEWAY_SERVICE_NAME + NOTIFICATION_PATH + "/notification/send-email/" + notificationId,
                    null,
                    String.class);
            log.info("E‑mail simulado enviado para a notificação id={}", notificationId);

        } catch (RestClientException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Falha inesperada ao enviar notificação", e);
        }
    }

    @SuppressWarnings("unused")
    private void fallbackCreateAndSend(String userID, String userEmail, String message, OffsetDateTime dateTime, Throwable t) {
        log.error("Falha ao notificar (fallback via Gateway): userID={}, email={}, mensagem={}, erro: {}",
                userID, userEmail, message, t.getMessage(), t);
    }
}