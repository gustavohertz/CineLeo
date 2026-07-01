package com.cinelo.app.dto;

import lombok.Data;

@Data
public class AvaliacaoResponseDTO {
    private String id;
    private String filmeId;
    private String usuarioNome;
    private Double nota;
    private String comentario;
}
