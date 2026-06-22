package com.leocine.service;

import com.leocine.dto.NotificationRequestDTO;
import com.leocine.dto.NotificationResponseDTO;
import com.leocine.entity.NotificationMessage;
import com.leocine.exception.NotificationProcessingException;
import com.leocine.repository.NotificationJpaRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationJpaRepository notificationRepository;
    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;
    private final Map<String, NotificationResponseDTO> memoryStore = new ConcurrentHashMap<>();
    private final Set<String> emailSentIds = ConcurrentHashMap.newKeySet();
    private final Map<String, CompletableFuture<Void>> pendingConfirmations = new ConcurrentHashMap<>();

    public NotificationService(NotificationJpaRepository notificationRepository,
            KafkaTemplate<String, NotificationMessage> kafkaTemplate) {
        this.notificationRepository = notificationRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        OffsetDateTime dateTime = request.getDateTime() != null
                ? request.getDateTime()
                : OffsetDateTime.now();

        String generatedId = UUID.randomUUID().toString();

        NotificationMessage entity = new NotificationMessage();
        entity.setId(generatedId);
        entity.setUserID(request.getUserID());
        entity.setUserEmail(request.getUserEmail());
        entity.setMsgString(request.getMsgString());
        entity.setDateTime(dateTime);

        notificationRepository.save(entity);
        log.info("Notification saved to DB: id={}", generatedId);

        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setId(generatedId);
        response.setUserID(request.getUserID());
        response.setUserEmail(request.getUserEmail());
        response.setMsgString(request.getMsgString());
        response.setDateTime(dateTime);

        memoryStore.put(generatedId, response);
        log.debug("Notification cached: id={}", generatedId);

        return response;
    }

    public NotificationResponseDTO consumeNotification(NotificationRequestDTO request) {
        return createNotification(request);
    }

    public void sendEmailById(String id) {
        if (id == null) {
            throw new NotificationProcessingException("Notification id is required");
        }

        NotificationMessage entity = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationProcessingException("Notification not found for ID: " + id));

        if (entity.getSentAt() != null) {
            throw new NotificationProcessingException("E-mail already sent for notification id: " + id);
        }

        CompletableFuture<Void> confirmation = new CompletableFuture<>();
        pendingConfirmations.put(id, confirmation);

        try {
            kafkaTemplate.send("notification.email.send", entity).get(5, TimeUnit.SECONDS);
            log.info("Evento de envio de e-mail publicado para notificação id={}", id);
        } catch (Exception e) {
            pendingConfirmations.remove(id);
            log.error("Falha ao publicar evento de envio de e-mail para notificação id={}: {}", id, e.getMessage());
            throw new NotificationProcessingException("Failed to queue email for sending: " + e.getMessage());
        }

        try {
            confirmation.get(10, TimeUnit.SECONDS);
            entity.setSentAt(OffsetDateTime.now());
            notificationRepository.save(entity);
            emailSentIds.add(id);
            log.info("E-mail confirmado como enviado para notificação id={}", id);
        } catch (TimeoutException e) {
            pendingConfirmations.remove(id);
            log.error("Timeout aguardando confirmação de envio para notificação id={}", id);
            throw new NotificationProcessingException("Failed to send email: timeout waiting for confirmation");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            pendingConfirmations.remove(id);
            throw new NotificationProcessingException("Failed to send email: interrupted");
        } catch (ExecutionException e) {
            pendingConfirmations.remove(id);
            Throwable cause = e.getCause();
            log.error("Falha no envio de e-mail para notificação id={}: {}", id, cause.getMessage());
            throw new NotificationProcessingException("Failed to send email: " + cause.getMessage());
        }
    }

    @KafkaListener(topics = "notification.email.sent", groupId = "notification-service")
    public void handleEmailSentConfirmation(ConsumerRecord<String, String> record) {
        String id = record.key();
        String status = record.value();
        log.info("Confirmação de envio recebida: id={}, status={}", id, status);

        CompletableFuture<Void> future = pendingConfirmations.remove(id);
        if (future != null) {
            if ("SENT".equalsIgnoreCase(status)) {
                future.complete(null);
            } else {
                String errorMsg = status.startsWith("FAILED:") ? status.substring(7).trim() : "Unknown error";
                future.completeExceptionally(new NotificationProcessingException(errorMsg));
            }
        }
    }

    public NotificationResponseDTO getNotificationById(String id) {
        if (id == null) {
            throw new NotificationProcessingException("Notification id is required");
        }

        NotificationResponseDTO cached = memoryStore.get(id);
        if (cached != null) {
            return cached;
        }

        NotificationMessage entity = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationProcessingException("Notification not found for ID: " + id));

        NotificationResponseDTO response = mapToResponse(entity);
        memoryStore.put(id, response);
        return response;
    }

    private NotificationResponseDTO mapToResponse(NotificationMessage entity) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(entity.getId());
        dto.setUserID(entity.getUserID());
        dto.setUserEmail(entity.getUserEmail());
        dto.setMsgString(entity.getMsgString());
        dto.setDateTime(entity.getDateTime());
        return dto;
    }
}