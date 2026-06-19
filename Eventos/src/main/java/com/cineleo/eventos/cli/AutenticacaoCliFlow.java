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

    public void realizarCadastro(Scanner scanner) {
        System.out.println("\n[Cadastro de Usuário]");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        UsuarioClient.UsuarioDTO dto = UsuarioClient.UsuarioDTO.builder()
                .nome(nome)
                .email(email)
                .cpf(cpf)
                .build();

        System.out.println("Criando usuário...");
        try {
            UsuarioClient.UsuarioDTO usuarioCriado = usuarioClient.criarUsuario(dto);
            System.out.println("Usuário criado com sucesso! ID: " + usuarioCriado.getId());

            cliState.setUsuarioLogado(usuarioCriado);
            cliState.setUsuarioLogadoId(usuarioCriado.getId());
            System.out.println("Login realizado automaticamente! Bem-vindo, " + usuarioCriado.getNome());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }
}
