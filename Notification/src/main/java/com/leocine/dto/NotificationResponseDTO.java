package com.leocine.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class NotificationResponseDTO {

    private String id;
    private String userID;
    private String userEmail;
    private String msgString;
    private OffsetDateTime dateTime;

}

