package com.cinelo.app.cli;

import com.cinelo.app.client.UsuarioClient;
import com.cinelo.app.dto.LoginResponseDTO;
import com.cinelo.app.dto.UsuarioDTO;
import com.cinelo.app.dto.UsuarioRequestDTO;
import com.cinelo.app.exception.BusinessException;
import com.cinelo.app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class AutenticacaoCliFlow {

    private final UsuarioClient usuarioClient;
    private final CliState cliState;

    public boolean garantirLogin(Scanner scanner) {
        if (cliState.isLogado()) {
            return true;
        }
        return realizarLogin(scanner);
    }

    public boolean realizarLogin(Scanner scanner) {
        if (cliState.isLogado()) {
            System.out.println("\nVocê já está logado como: " + cliState.getUsuarioLogado().getNome());
            System.out.println("Deseja trocar de conta? (S/N)");
            if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
                return true;
            }
        }

        while (true) {
            System.out.println("\n[Login]");
            System.out.print("E-mail: ");
            String email = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            try {
                LoginResponseDTO loginResponse = usuarioClient.login(email, senha);
                String token = loginResponse.accessToken();
                long expiresIn = loginResponse.expiresIn();

                Long userId = Long.parseLong(JwtUtil.getSubject(token));
                UsuarioDTO usuario = usuarioClient.buscarPorId(userId);

                cliState.setToken(token, expiresIn);
                cliState.setUsuarioLogado(usuario);
                cliState.setUsuarioLogadoId(usuario.getId());

                System.out.println("Login realizado com sucesso! Bem-vindo, " + usuario.getNome());
                return true;

            } catch (BusinessException e) {
                System.out.println("Falha no login: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado. Tente novamente.");
            }

            System.out.print("Deseja tentar novamente? (S/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
                return false;
            }
        }
    }

    public void realizarCadastro(Scanner scanner) {
        while (true) {
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

            UsuarioRequestDTO request = new UsuarioRequestDTO();
            request.setNome(nome);
            request.setIdade(idade);
            request.setEmail(email);
            request.setCpf(cpf);
            request.setSenha(senha);

            System.out.println("Criando usuário...");
            try {
                UsuarioDTO usuarioCriado = usuarioClient.criarUsuario(request);
                System.out.println("Usuário criado com sucesso! ID: " + usuarioCriado.getId());

                LoginResponseDTO loginResponse = usuarioClient.login(email, senha);
                cliState.setToken(loginResponse.accessToken(), loginResponse.expiresIn());
                cliState.setUsuarioLogado(usuarioCriado);
                cliState.setUsuarioLogadoId(usuarioCriado.getId());

                System.out.println("Login realizado automaticamente! Bem-vindo, " + usuarioCriado.getNome());
                return;

            } catch (BusinessException e) {
                System.out.println("Erro ao cadastrar: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado. Tente novamente.");
            }

            System.out.print("Deseja tentar novamente? (S/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
                System.out.println("Cadastro cancelado.");
                return;
            }
        }
    }
}