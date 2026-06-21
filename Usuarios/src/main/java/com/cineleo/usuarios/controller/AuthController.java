package com.cineleo.usuarios.controller;

import com.cineleo.usuarios.dto.LoginRequestDTO;
import com.cineleo.usuarios.dto.UsuarioResponseDTO;
import com.cineleo.usuarios.entity.Usuario;
import com.cineleo.usuarios.exception.BusinessException;
import com.cineleo.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Email ou senha invalidos"));

        if (!usuario.getCpf().equals(dto.getSenha())) {
            throw new BusinessException("Email ou senha invalidos");
        }

        if (usuario.getStatus() == Usuario.StatusUsuario.INATIVO) {
            throw new BusinessException("Usuario inativo");
        }

        return ResponseEntity.ok(UsuarioResponseDTO.from(usuario));
    }
}
