package com.cineleo.usuarios.dto;

import com.cineleo.usuarios.entity.UsuarioEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private Integer idade;
    private String email;
    private String cpf;
    private boolean ativo;
    private LocalDateTime criadoEm;
    private Set<String> roles;
    private String customerId;

    public static UsuarioResponseDTO from(UsuarioEntity usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .idade(usuario.getIdade())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .ativo(usuario.isAtivo())
                .criadoEm(usuario.getCriadoEm())
                .roles(usuario.getRoles())
                .customerId(usuario.getCustomerId())   // NOVO
                .build();
    }
}