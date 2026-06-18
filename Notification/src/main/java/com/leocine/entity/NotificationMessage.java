package com.leocine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "notification_messages")
public class NotificationMessage {

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userID;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "msg_string", nullable = false, columnDefinition = "TEXT")
    private String msgString;

    @Column(name = "date_time", nullable = false)
    private OffsetDateTime dateTime;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    public NotificationMessage() {
    }
}