package com.leocine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    public NotificationMessage() {
    }

    public NotificationMessage(String id, String userID, String userEmail, String msgString, OffsetDateTime dateTime) {
        this.id = id;
        this.userID = userID;
        this.userEmail = userEmail;
        this.msgString = msgString;
        this.dateTime = dateTime;
    }
}

