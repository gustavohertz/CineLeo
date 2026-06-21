// Pagamento/src/main/java/com/cineleo/service/NotificationClientService.java
package com.cineleo.service;

import com.cineleo.dto.ConsumeResponseDTO;
import com.cineleo.dto.NotificationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationClientService {

    private static final Logger log = LoggerFactory.getLogger(NotificationClientService.class);

    private final RestTemplate restTemplate;
    private final String notificationApiUrl;

    public NotificationClientService(RestTemplate restTemplate,
            @Value("${notification.api.url}") String notificationApiUrl) {
        this.restTemplate = restTemplate;
        this.notificationApiUrl = notificationApiUrl;
    }

    public void createAndSendEmail(String userID, String userEmail, String message, OffsetDateTime dateTime) {
        try {
            
            NotificationRequestDTO request = new NotificationRequestDTO(
                    userID,
                    userEmail,
                    message,
                    dateTime != null ? dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : null);

            ConsumeResponseDTO response = restTemplate.postForObject(
                    notificationApiUrl + "/notification/consume",
                    request,
                    ConsumeResponseDTO.class);

            if (response == null || response.id() == null) {
                log.warn("Resposta inesperada ao criar notificação: {}", response);
                return;
            }

            String notificationId = response.id();
            log.info("Notificação criada com sucesso: id={}", notificationId);

            // email fake enviado
            restTemplate.postForObject(
                    notificationApiUrl + "/notification/send-email/" + notificationId,
                    null,
                    String.class);
            log.info("E‑mail simulado enviado para a notificação id={}", notificationId);

        } catch (Exception e) {
            log.error("Falha ao criar/enviar notificação: {}", e.getMessage(), e);
        }
    }
}