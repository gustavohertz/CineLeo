package com.cinelo.app.cli;

import com.cinelo.app.dto.UsuarioDTO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Data
public class CliState {
    private Long usuarioLogadoId;
    private UsuarioDTO usuarioLogado;
    private String token;
    private Instant tokenExpiration;

    public boolean isLogado() {
        return token != null &&
                tokenExpiration != null &&
                tokenExpiration.isAfter(Instant.now()) &&
                usuarioLogado != null;
    }

    public void setToken(String token, long expiresInSeconds) {
        this.token = token;
        this.tokenExpiration = Instant.now().plusSeconds(expiresInSeconds);
    }

    public void logout() {
        this.token = null;
        this.tokenExpiration = null;
        this.usuarioLogado = null;
        this.usuarioLogadoId = null;
    }
}