package com.infnet.microservicesKafka.client;

import com.infnet.microservicesKafka.dto.ConsumeResponseDTO;
import com.infnet.microservicesKafka.dto.NotificationRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestTemplate restTemplate;
    private final String notificationUrl;

    public NotificationClient(RestTemplate restTemplate,
                              @Value("${services.notification.url}") String notificationUrl) {
        this.restTemplate = restTemplate;
        this.notificationUrl = notificationUrl;
    }

    public void createAndSendEmail(String userID, String userEmail, String message, String dateTime) {
        try {
            NotificationRequestDTO request = new NotificationRequestDTO(userID, userEmail, message, dateTime);
            ConsumeResponseDTO response = restTemplate.postForObject(
                    notificationUrl + "/notification/consume", request, ConsumeResponseDTO.class);

            if (response != null && response.id() != null) {
                log.info("Notificação criada: id={}", response.id());
                restTemplate.postForObject(
                        notificationUrl + "/notification/send-email/" + response.id(),
                        null, String.class);
                log.info("E‑mail disparado para notificação {}", response.id());
            }
        } catch (Exception e) {
            log.error("Erro ao processar notificação para evento: {}", e.getMessage());
        }
    }
}