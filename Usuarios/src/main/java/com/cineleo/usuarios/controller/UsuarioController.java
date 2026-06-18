package com.cineleo.usuarios.controller;

import com.cineleo.usuarios.dto.LoginReponseDTO;
import com.cineleo.usuarios.dto.LoginRequestDTO;
import com.cineleo.usuarios.dto.UsuarioRequestDTO;
import com.cineleo.usuarios.dto.UsuarioResponseDTO;
import com.cineleo.usuarios.service.AuthService;
import com.cineleo.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    @GetMapping("/all")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // Rota de cadastro com senha
    @PostMapping("/create")
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody @Valid UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(dto));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<LoginReponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        LoginReponseDTO login = authService.login(request);
        return ResponseEntity.ok(login);
    }
}