package com.cinelo.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDTO {
    private Long id;
    private String nome;
    private Integer idade;
    private String email;
    private String cpf;
    private boolean ativo;
    private String status;
}