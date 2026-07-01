package com.cinelo.app.cli;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class MenuInterativoRunner implements CommandLineRunner {

    private final CatalogoCliFlow catalogoCliFlow;
    private final IngressosCliFlow ingressosCliFlow;
    private final ContaCliFlow contaCliFlow;
    private final AutenticacaoCliFlow autenticacaoCliFlow;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=================================");
            System.out.println("   INÍCIO - Boas-vindas ao CineLeo");
            System.out.println("=================================");
            System.out.println("Digite a opção desejada:");
            System.out.println("1 - Ver Catálogo de Filmes");
            System.out.println("2 - Meus Ingressos");
            System.out.println("3 - Minha Conta");
            System.out.println("4 - Fazer Login / Trocar de Conta");
            System.out.println("5 - Fazer Cadastro");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine();

            try {
                switch (opcao) {
                    case "1":
                        catalogoCliFlow.executar(scanner);
                        break;
                    case "2":
                        ingressosCliFlow.executar(scanner);
                        break;
                    case "3":
                        contaCliFlow.executar(scanner);
                        break;
                    case "4":
                        autenticacaoCliFlow.realizarLogin(scanner);
                        break;
                    case "5":
                        autenticacaoCliFlow.realizarCadastro(scanner);
                        break;
                    case "0":
                        System.out.println("Saindo do terminal do CineLeo...");
                        return;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                }
            } catch (Exception e) {
                System.out.println("\n[ERRO] Ocorreu um erro na operação: " + e.getMessage());
                System.out.println("Verifique se os serviços (Gateway, Eventos, Usuarios, Pagamento) estão rodando.");
            }
        }
    }
}

