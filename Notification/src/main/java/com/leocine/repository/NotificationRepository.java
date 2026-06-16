package com.leocine.repository;

import com.leocine.entity.NotificationMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Map;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Repository;

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
        } else if (dateTimeObj == null) {
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

        return Map.of(
                "id", found.getId(),
                "userID", found.getUserID(),
                "userEmail", found.getUserEmail(),
                "msgString", found.getMsgString(),
                "dateTime", found.getDateTime()
        );
    }
}

