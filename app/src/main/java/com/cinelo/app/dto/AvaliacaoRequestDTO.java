package com.cinelo.app.dto;

import lombok.Data;

@Data
public class AvaliacaoRequestDTO {
    private String filmeId;
    private Double nota;
    private String comentario;
}
