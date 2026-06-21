package com.cineleo.usuarios.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String senha;
}
