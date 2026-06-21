package com.cineleo.eventos.cli;

import com.cineleo.eventos.client.UsuarioClient;
import com.cineleo.eventos.client.UsuarioClient.LoginResponse;
import com.cineleo.eventos.client.UsuarioClient.UsuarioDTO;
import com.cineleo.eventos.client.UsuarioClient.UsuarioRequest;
import com.cineleo.eventos.util.JwtUtil;
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
            realizarLogin(scanner);
        }
    }

    public void realizarLogin(Scanner scanner) {
        if (cliState.isLogado()) {
            System.out.println("\nVocê já está logado como: " + cliState.getUsuarioLogado().getNome());
            System.out.println("Deseja trocar de conta? (S/N)");
            if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
                return;
            }
        }

        System.out.println("\n[Login]");
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        try {
            LoginResponse loginResponse = usuarioClient.login(email, senha);
            String token = loginResponse.getAccessToken();
            long expiresIn = loginResponse.getExpiresIn();

            // Extrai ID do token
            Long userId = Long.parseLong(JwtUtil.getSubject(token));

            // Busca dados completos
            UsuarioDTO usuario = usuarioClient.buscarPorId(userId);

            cliState.setToken(token, expiresIn);
            cliState.setUsuarioLogado(usuario);
            cliState.setUsuarioLogadoId(usuario.getId());

            System.out.println("Login realizado com sucesso! Bem-vindo, " + usuario.getNome());
        } catch (Exception e) {
            System.out.println("Falha no login: " + e.getMessage());
        }
    }

    public void realizarCadastro(Scanner scanner) {
        System.out.println("\n[Cadastro de Usuário]");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Idade: ");
        int idade = Integer.parseInt(scanner.nextLine());

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("CPF (apenas números): ");
        String cpf = scanner.nextLine();

        System.out.print("Senha (mínimo 8 caracteres): ");
        String senha = scanner.nextLine();

        UsuarioRequest request = new UsuarioRequest();
        request.setNome(nome);
        request.setIdade(idade);
        request.setEmail(email);
        request.setCpf(cpf);
        request.setSenha(senha);

        System.out.println("Criando usuário...");
        try {
            UsuarioDTO usuarioCriado = usuarioClient.criarUsuario(request);
            System.out.println("Usuário criado com sucesso! ID: " + usuarioCriado.getId());

            // Login automático
            LoginResponse loginResponse = usuarioClient.login(email, senha);
            cliState.setToken(loginResponse.getAccessToken(), loginResponse.getExpiresIn());
            cliState.setUsuarioLogado(usuarioCriado);
            cliState.setUsuarioLogadoId(usuarioCriado.getId());

            System.out.println("Login realizado automaticamente! Bem-vindo, " + usuarioCriado.getNome());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }
}