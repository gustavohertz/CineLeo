package com.cineleo.dto;

public record NotificationRequestDTO(
        String userID,
        String userEmail,
        String msgString,
        String dateTime) {
}