package com.cineleo.cli.menu;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuSessoes {

    private final String baseUrl;
    private final Scanner sc;

    public MenuSessoes(String baseUrl, Scanner sc) {
        this.baseUrl = baseUrl;
        this.sc = sc;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n===== SESSOES =====");
            System.out.println("1. Listar todas");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Listar sessoes de um filme");
            System.out.println("4. Cadastrar sessao");
            System.out.println("5. Cancelar sessao");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1" -> listar();
                case "2" -> buscarPorId();
                case "3" -> listarPorFilme();
                case "4" -> cadastrar();
                case "5" -> cancelar();
                case "0" -> voltar = true;
                default -> System.out.println("  Opcao invalida.");
            }
        }
    }

    private void listar() {
        JsonNode res = HttpHelper.get(baseUrl + "/sessoes");
        System.out.println("\n--- Sessoes ---");
        if (res.isArray() && res.size() == 0) { System.out.println("  Nenhuma sessao cadastrada."); return; }
        if (res.isArray()) { for (JsonNode s : res) imprimirSessao(s); }
        else System.out.println("  " + res);
    }

    private void buscarPorId() {
        System.out.print("  ID da sessao: ");
        String id = sc.nextLine().trim();
        imprimirSessao(HttpHelper.get(baseUrl + "/sessoes/" + id));
    }

    private void listarPorFilme() {
        System.out.print("  ID do filme: ");
        String id = sc.nextLine().trim();
        JsonNode res = HttpHelper.get(baseUrl + "/sessoes/filme/" + id);
        System.out.println("\n--- Sessoes disponiveis ---");
        if (res.isArray() && res.size() == 0) { System.out.println("  Nenhuma sessao disponivel."); return; }
        if (res.isArray()) { for (JsonNode s : res) imprimirSessao(s); }
        else System.out.println("  " + res);
    }

    private void cadastrar() {
        System.out.println("\n  -- Nova Sessao --");
        System.out.print("  ID do filme: ");
        long filmeId = Long.parseLong(sc.nextLine().trim());
        System.out.print("  ID da sala: ");
        long salaId = Long.parseLong(sc.nextLine().trim());
        System.out.print("  Data/hora inicio (ex: 2026-06-20T19:00:00): ");
        String inicio = sc.nextLine().trim();
        System.out.print("  Data/hora fim   (ex: 2026-06-20T21:00:00): ");
        String fim = sc.nextLine().trim();
        System.out.print("  Preco (ex: 25.00): ");
        double preco = Double.parseDouble(sc.nextLine().trim());

        Map<String, Object> body = new HashMap<>();
        body.put("filmeId", filmeId);
        body.put("salaId", salaId);
        body.put("dataHoraInicio", inicio);
        body.put("dataHoraFim", fim);
        body.put("preco", preco);

        JsonNode res = HttpHelper.post(baseUrl + "/sessoes", body);
        System.out.println("  Sessao criada:");
        imprimirSessao(res);
    }

    private void cancelar() {
        System.out.print("  ID da sessao a cancelar: ");
        String id = sc.nextLine().trim();
        JsonNode res = HttpHelper.patch(baseUrl + "/sessoes/" + id + "/cancelar");
        System.out.println("  Sessao cancelada:");
        imprimirSessao(res);
    }

    private void imprimirSessao(JsonNode s) {
        if (s.has("erro")) { System.out.println("  Erro: " + s.get("erro").asText()); return; }
        System.out.printf("  [%d] %s | Sala: %s | Inicio: %s | Preco: R$ %.2f | Assentos: %d | %s%n",
                s.path("id").asLong(),
                s.path("filmeNome").asText(),
                s.path("salaNome").asText(),
                s.path("dataHoraInicio").asText().replace("T", " "),
                s.path("preco").asDouble(),
                s.path("assentosDisponiveis").asInt(),
                s.path("status").asText());
    }
}
