package com.infnet.microservicesKafka.consumer;

import com.infnet.microservicesKafka.dto.NotificationEmailEvent;
import com.infnet.microservicesKafka.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationEmailConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEmailConsumer.class);

    private final EmailSenderService emailSenderService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public NotificationEmailConsumer(EmailSenderService emailSenderService, KafkaTemplate<String, String> kafkaTemplate) {
        this.emailSenderService = emailSenderService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "notification.email.send", groupId = "cineleo-email-sender")
    public void handleEmailRequest(NotificationEmailEvent event) {
        log.info("Recebido pedido de envio de e‑mail para: {}", event.getUserEmail());
        try {
            emailSenderService.sendEmail(
                    event.getUserEmail(),
                    "Notificação CineLeo",
                    event.getMsgString()
            );
            log.info("E‑mail enviado com sucesso para {}", event.getUserEmail());
            kafkaTemplate.send("notification.email.sent", event.getId(), "SENT");
        } catch (Exception e) {
            log.error("Falha ao enviar e‑mail para {}: {}", event.getUserEmail(), e.getMessage());
            kafkaTemplate.send("notification.email.sent", event.getId(), "FAILED: " + e.getMessage());
        }
    }
}