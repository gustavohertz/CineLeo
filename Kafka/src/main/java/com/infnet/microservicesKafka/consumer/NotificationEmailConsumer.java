package com.infnet.microservicesKafka.consumer;

import com.infnet.microservicesKafka.dto.NotificationEmailEvent;
import com.infnet.microservicesKafka.service.EmailSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
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

    @RetryableTopic(
            attempts = "2",
            backoff = @Backoff(delay = 1000),
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )

    @KafkaListener(topics = "notification.email.send", groupId = "cineleo-email-sender")
    public void handleEmailRequest(NotificationEmailEvent event) {
        log.info("Recebido pedido de envio de e‑mail para: {}", event.getUserEmail());
        emailSenderService.sendEmail(
                event.getUserEmail(),
                "Notificação CineLeo",
                event.getMsgString()
        );

        log.info("E-mail enviado com sucesso para {}", event.getUserEmail());
        kafkaTemplate.send("notification.email.sent", event.getId(), "SENT");
    }
}