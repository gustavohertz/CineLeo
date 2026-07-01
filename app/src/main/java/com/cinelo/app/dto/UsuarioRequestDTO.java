package com.cinelo.app.dto;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String nome;
    private Integer idade;
    private String email;
    private String cpf;
    private String senha;
}