package com.cineleo.usuarios.dto;

import java.util.List;

public record UsuarioAutenticadoResponseDTO(String id, String nome, String email, List<String> roles){

}
