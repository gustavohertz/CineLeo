package com.leocine.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
public class NotificationRequestDTO {

    @NotBlank(message = "User ID is required")
    private String userID;

    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    @NotBlank(message = "Message string is required")
    private String msgString;

    private OffsetDateTime dateTime;
}