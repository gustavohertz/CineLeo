package com.leocine.service;

import com.leocine.dto.NotificationRequestDTO;
import com.leocine.dto.NotificationResponseDTO;
import com.leocine.exception.NotificationProcessingException;
import com.leocine.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final Map<String, NotificationResponseDTO> memoryStore = new ConcurrentHashMap<>();

    private final Set<String> emailSentIds = ConcurrentHashMap.newKeySet();

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        if (request == null) {
            throw new NotificationProcessingException("Notification request is required");
        }

        OffsetDateTime dateTime = request.getDateTime() != null ? request.getDateTime() : OffsetDateTime.now();
        String generatedId = UUID.randomUUID().toString();

        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setId(generatedId);
        response.setUserID(request.getUserID());
        response.setUserEmail(request.getUserEmail());
        response.setMsgString(request.getMsgString());
        response.setDateTime(dateTime);

        memoryStore.put(response.getId(), response);

        try {
            Map<String, Object> notificationData = Map.of(
                    "id", response.getId(),
                    "userID", response.getUserID(),
                    "userEmail", response.getUserEmail(),
                    "msgString", response.getMsgString(),
                    "dateTime", response.getDateTime()
            );
            notificationRepository.save(notificationData);
        } catch (Exception e) {
            throw new NotificationProcessingException("Failed to save notification", e);
        }

        return response;
    }

    public NotificationResponseDTO consumeNotification(NotificationRequestDTO request) {
        return createNotification(request);
    }

    public String sendEmailById(String id) {
        if (id == null || id.isBlank()) {
            throw new NotificationProcessingException("Notification id is required");
        }

        if (!emailSentIds.add(id)) {
            throw new NotificationProcessingException("E-mail already sent for notification id: " + id);
        }

        NotificationResponseDTO notification = getNotificationById(id);
        if (notification == null) {
            throw new NotificationProcessingException("Notification not found for ID: " + id);
        }

        return "ok";
    }


    public NotificationResponseDTO getNotificationById(String id) {
        if (id == null || id.isBlank()) {
            throw new NotificationProcessingException("Notification id is required");
        }

        NotificationResponseDTO found = memoryStore.get(id);
        if (found != null) {
            return found;
        }

        try {
            Map<String, Object> notificationData = notificationRepository.findById(id);
            if (notificationData == null) {
                throw new NotificationProcessingException("Notification not found for ID: " + id);
            }

            NotificationResponseDTO response = new NotificationResponseDTO();
            response.setId((String) notificationData.get("id"));
            response.setUserID((String) notificationData.get("userID"));
            response.setUserEmail((String) notificationData.get("userEmail"));
            response.setMsgString((String) notificationData.get("msgString"));

            Object dateTimeObj = notificationData.get("dateTime");
            if (dateTimeObj instanceof OffsetDateTime) {
                response.setDateTime((OffsetDateTime) dateTimeObj);
            }

            memoryStore.put(response.getId(), response);
            return response;
        } catch (NotificationProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new NotificationProcessingException("Failed to retrieve notification", e);
        }

    }

}

