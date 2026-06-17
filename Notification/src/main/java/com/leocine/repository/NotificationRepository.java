package com.leocine.repository;

import com.leocine.entity.NotificationMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class NotificationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Map<String, Object> notificationData) {
        if (notificationData == null) {
            return;
        }

        NotificationMessage message = new NotificationMessage();
        message.setId((String) notificationData.get("id"));
        message.setUserID((String) notificationData.get("userID"));
        message.setUserEmail((String) notificationData.get("userEmail"));
        message.setMsgString((String) notificationData.get("msgString"));

        Object dateTimeObj = notificationData.get("dateTime");
        if (dateTimeObj instanceof OffsetDateTime) {
            message.setDateTime((OffsetDateTime) dateTimeObj);
        } else {
            message.setDateTime(OffsetDateTime.now());
        }

        if (message.getId() != null && entityManager.find(NotificationMessage.class, message.getId()) != null) {
            entityManager.merge(message);
        } else {
            entityManager.persist(message);
        }
    }

    @Transactional
    public Map<String, Object> findById(String id) {
        NotificationMessage found = entityManager.find(NotificationMessage.class, id);
        if (found == null) {
            return null;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", found.getId());
        response.put("userID", found.getUserID());
        response.put("userEmail", found.getUserEmail());
        response.put("msgString", found.getMsgString());
        response.put("dateTime", found.getDateTime());

        return response;
    }
}