package com.infnet.microservicesKafka.dto;

public record NotificationRequestDTO(
        String userID,
        String userEmail,
        String msgString,
        String dateTime
) {}