package com.cineleo.eventos.cli;

import com.cineleo.eventos.client.UsuarioClient;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class CliState {
    private Long usuarioLogadoId;
    private UsuarioClient.UsuarioDTO usuarioLogado;

    public boolean isLogado() {
        return usuarioLogadoId != null && usuarioLogado != null;
    }
}
