package com.cineleo.eventos.cli;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ContaCliFlow {

    private final AutenticacaoCliFlow autenticacaoCliFlow;
    private final CliState cliState;

    public void executar(Scanner scanner) {
        System.out.println("\n--- Minha Conta ---");
        if (!autenticacaoCliFlow.garantirLogin(scanner)) {
            System.out.println("Login necessário para acessar sua conta.");
            return;
        }

        System.out.println("Nome: " + cliState.getUsuarioLogado().getNome());
        System.out.println("CPF: " + cliState.getUsuarioLogado().getCpf());
        System.out.println("E-mail: " + cliState.getUsuarioLogado().getEmail());
        System.out.println("Status: " + cliState.getUsuarioLogado().getStatus());
        System.out.println("\nPressione ENTER para voltar...");
        scanner.nextLine();
    }
}