package com.leocine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumeResponseDTO {

    private String id;
    private String status;

    public ConsumeResponseDTO(String id, String status) {
        this.id = id;
        this.status = status;
    }

}

