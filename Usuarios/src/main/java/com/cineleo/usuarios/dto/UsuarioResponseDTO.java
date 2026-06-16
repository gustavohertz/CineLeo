package com.cineleo.usuarios.dto;

import com.cineleo.usuarios.entity.Usuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private Integer idade;
    private String email;
    private String cpf;
    private Usuario.StatusUsuario status;
    private LocalDateTime criadoEm;

    public static UsuarioResponseDTO from(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .idade(usuario.getIdade())
                .email(usuario.getEmail())
                .cpf(usuario.getCpf())
                .status(usuario.getStatus())
                .criadoEm(usuario.getCriadoEm())
                .build();
    }
}
