package com.cineleo.usuarios.service;

import com.cineleo.usuarios.dto.LoginReponseDTO;
import com.cineleo.usuarios.dto.LoginRequestDTO;
import com.cineleo.usuarios.entity.UsuarioEntity;
import com.cineleo.usuarios.exception.CredenciaisInvalidasException;
import com.cineleo.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginReponseDTO login(LoginRequestDTO request) {
        UsuarioEntity usuario = usuarioRepository
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new CredenciaisInvalidasException("Credenciais inválidas"));

        if (!usuario.isAtivo()) {
            throw new CredenciaisInvalidasException("Usuário inativo");
        }

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException("Credenciais inválidas");
        }

        String accessToken = jwtService.gerarAccessToken(usuario);
        long expiresIn = jwtService.getExpirationSeconds();
        return new LoginReponseDTO(accessToken, "Bearer", expiresIn);
    }
}