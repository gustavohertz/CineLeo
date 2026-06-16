package com.leocine.service;

import com.leocine.dto.NotificationRequestDTO;
import com.leocine.dto.NotificationResponseDTO;
import com.leocine.exception.NotificationProcessingException;
import com.leocine.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Persistência em memória para habilitar consulta por ID sem depender do Repository.
    private final Map<String, NotificationResponseDTO> memoryStore = new ConcurrentHashMap<>();

    // Regra de negócio: enviar e-mail por ID apenas 1 vez.
    private final Set<String> emailSentIds = ConcurrentHashMap.newKeySet();


    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponseDTO createNotification(NotificationRequestDTO request) {
        if (request == null) {
            throw new NotificationProcessingException("Notification request is required");
        }
        if (request.getId() == null || request.getId().isBlank()) {
            throw new NotificationProcessingException("Notification id is required");
        }

        OffsetDateTime dateTime = request.getDateTime() != null ? request.getDateTime() : OffsetDateTime.now();

        NotificationResponseDTO response = new NotificationResponseDTO();
        response.setId(request.getId());
        response.setUserID(request.getUserID());
        response.setUserEmail(request.getUserEmail());
        response.setMsgString(request.getMsgString());
        response.setDateTime(dateTime);

        // Atualiza store em memória para consulta imediata
        memoryStore.put(response.getId(), response);

        // Mantém chamada para o repository (não alterado). Se ainda não persistir de verdade, não quebra o fluxo.
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
            // Não impedir a operação por causa do repository ainda stubado.
        }

        return response;
    }

    // consume e ingestão
    public NotificationResponseDTO consumeNotification(NotificationRequestDTO request) {
        return createNotification(request);
    }

    // Enviar e-mail por ID apenas 1 vez.
    public String sendEmailById(String id) {
        if (id == null || id.isBlank()) {
            throw new NotificationProcessingException("Notification id is required");
        }

        if (!emailSentIds.add(id)) {
            throw new NotificationProcessingException("E-mail already sent for notification id: " + id);
        }

        // valida que a notificação existe (consulta por ID imprime os dados)
        NotificationResponseDTO notification = getNotificationById(id);
        if (notification == null) {
            throw new NotificationProcessingException("Notification not found for ID: " + id);
        }

        // Integração real com e-mail fica a cargo de um provedor; por enquanto, sinalizamos sucesso.
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

        // fallback para o repository (caso você futuramente implemente persistência real)
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

