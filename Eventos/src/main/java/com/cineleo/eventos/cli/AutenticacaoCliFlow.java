package com.cineleo.eventos.cli;

import com.cineleo.eventos.client.UsuarioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class AutenticacaoCliFlow {

    private final UsuarioClient usuarioClient;
    private final CliState cliState;

    public void garantirLogin(Scanner scanner) {
        if (!cliState.isLogado()) {
            realizarLoginMock(scanner);
        }
    }

    public void realizarLoginMock(Scanner scanner) {
        if (cliState.isLogado()) {
            System.out.println("\nVocê já está logado como: " + cliState.getUsuarioLogado().getNome());
            System.out.println("Deseja trocar de conta? (S/N)");
            if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
                return;
            }
        }
        
        System.out.println("\n[Login Necessário]");
        System.out.println("Por favor, informe o seu ID de Usuário (O serviço de Usuários será consultado):");
        System.out.print("ID: ");
        Long id = Long.parseLong(scanner.nextLine());

        System.out.println("Buscando usuário...");
        UsuarioClient.UsuarioDTO usuario = usuarioClient.buscarPorId(id);
        
        cliState.setUsuarioLogado(usuario);
        cliState.setUsuarioLogadoId(usuario.getId());

        System.out.println("Login realizado com sucesso! Bem-vindo, " + usuario.getNome());
    }
}
