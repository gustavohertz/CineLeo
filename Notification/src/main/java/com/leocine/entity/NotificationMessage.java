package com.leocine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMsgString() {
        return msgString;
    }

    public void setMsgString(String msgString) {
        this.msgString = msgString;
    }

    public OffsetDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(OffsetDateTime dateTime) {
        this.dateTime = dateTime;
    }
}

