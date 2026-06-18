package com.leocine.service;

import com.leocine.dto.NotificationRequestDTO;
import com.leocine.dto.NotificationResponseDTO;
import com.leocine.entity.NotificationMessage;
import com.leocine.exception.NotificationProcessingException;
import com.leocine.repository.NotificationJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationJpaRepository notificationRepository;

    private final Map<String, NotificationResponseDTO> memoryStore = new ConcurrentHashMap<>();

    private final Set<String> emailSentIds = ConcurrentHashMap.newKeySet();

    public NotificationService(NotificationJpaRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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

        if (emailSentIds.contains(id)) {
            throw new NotificationProcessingException("E-mail already sent for notification id: " + id);
        }

        NotificationMessage entity = notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationProcessingException("Notification not found for ID: " + id));

        if (entity.getSentAt() != null) {
            emailSentIds.add(id);
            throw new NotificationProcessingException("E-mail already sent for notification id: " + id);
        }

        entity.setSentAt(OffsetDateTime.now());
        notificationRepository.save(entity);
        emailSentIds.add(id);
        log.info("Email sent for notification id={}", id);
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