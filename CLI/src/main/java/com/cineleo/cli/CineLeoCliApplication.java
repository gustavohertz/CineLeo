package com.cineleo.cli;

import com.cineleo.cli.menu.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.cineleo.cli.menu.HttpHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SpringBootApplication
public class CineLeoCliApplication implements CommandLineRunner {

    @Value("${services.eventos.url}")
    private String eventosUrl;

    @Value("${services.usuarios.url}")
    private String usuariosUrl;

    public static void main(String[] args) {
        SpringApplication.run(CineLeoCliApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner sc = new Scanner(System.in, "UTF-8");

        JsonNode usuarioLogado = fazerLogin(sc);

        MenuFilmes menuFilmes     = new MenuFilmes(eventosUrl, sc);
        MenuSalas menuSalas       = new MenuSalas(eventosUrl, sc);
        MenuSessoes menuSessoes   = new MenuSessoes(eventosUrl, sc);
        MenuUsuarios menuUsuarios = new MenuUsuarios(usuariosUrl, sc);
        MenuReservas menuReservas = new MenuReservas(eventosUrl, sc);

        boolean sair = false;
        while (!sair) {
            System.out.println("\n╔══════════════════════════╗");
            System.out.println("║       CINELEO - MENU     ║");
            System.out.println("╠══════════════════════════╣");
            System.out.printf( "║ Ola, %-19s║%n", usuarioLogado.path("nome").asText());
            System.out.println("╠══════════════════════════╣");
            System.out.println("║ 1. Filmes                ║");
            System.out.println("║ 2. Salas                 ║");
            System.out.println("║ 3. Sessoes               ║");
            System.out.println("║ 4. Usuarios              ║");
            System.out.println("║ 5. Reservas / Pagamento  ║");
            System.out.println("║ 0. Sair                  ║");
            System.out.println("╚══════════════════════════╝");
            System.out.print("Opcao: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1" -> menuFilmes.exibir();
                case "2" -> menuSalas.exibir();
                case "3" -> menuSessoes.exibir();
                case "4" -> menuUsuarios.exibir();
                case "5" -> menuReservas.exibir();
                case "0" -> { sair = true; System.out.println("Ate logo!"); }
                default  -> System.out.println("  Opcao invalida.");
            }
        }

        sc.close();
    }

    private JsonNode fazerLogin(Scanner sc) {
        while (true) {
            System.out.println("\n╔══════════════════════════╗");
            System.out.println("║     CINELEO - LOGIN      ║");
            System.out.println("╚══════════════════════════╝");
            System.out.print("  Email: ");
            String email = sc.nextLine().trim();
            System.out.print("  Senha (CPF): ");
            String senha = sc.nextLine().trim();

            Map<String, Object> body = new HashMap<>();
            body.put("email", email);
            body.put("senha", senha);

            JsonNode res = HttpHelper.post(usuariosUrl + "/auth/login", body);

            if (res.has("erro") || res.has("mensagem")) {
                System.out.println("  Email ou senha invalidos. Tente novamente.");
            } else {
                System.out.println("  Login realizado com sucesso!");
                return res;
            }
        }
    }
}
