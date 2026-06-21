package com.cineleo.cli.menu;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuSalas {

    private final String baseUrl;
    private final Scanner sc;

    public MenuSalas(String baseUrl, Scanner sc) {
        this.baseUrl = baseUrl;
        this.sc = sc;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n===== SALAS =====");
            System.out.println("1. Listar todas");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Cadastrar sala");
            System.out.println("4. Atualizar sala");
            System.out.println("5. Deletar sala");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1" -> listar();
                case "2" -> buscarPorId();
                case "3" -> cadastrar();
                case "4" -> atualizar();
                case "5" -> deletar();
                case "0" -> voltar = true;
                default -> System.out.println("  Opcao invalida.");
            }
        }
    }

    private void listar() {
        JsonNode res = HttpHelper.get(baseUrl + "/salas");
        System.out.println("\n--- Salas ---");
        if (res.isArray() && res.size() == 0) { System.out.println("  Nenhuma sala cadastrada."); return; }
        if (res.isArray()) { for (JsonNode s : res) imprimirSala(s); }
        else System.out.println("  " + res);
    }

    private void buscarPorId() {
        System.out.print("  ID da sala: ");
        String id = sc.nextLine().trim();
        imprimirSala(HttpHelper.get(baseUrl + "/salas/" + id));
    }

    private void cadastrar() {
        System.out.println("\n  -- Nova Sala --");
        System.out.print("  Nome: ");
        String nome = sc.nextLine().trim();
        System.out.print("  Capacidade: ");
        int capacidade = Integer.parseInt(sc.nextLine().trim());
        System.out.println("  Tipo: 1. STANDARD  2. IMAX  3. DOLBY  4. VIP  5. DRIVE_IN");
        System.out.print("  Opcao: ");
        String[] tipos = {"STANDARD", "IMAX", "DOLBY", "VIP", "DRIVE_IN"};
        int ti = Integer.parseInt(sc.nextLine().trim()) - 1;
        String tipo = (ti >= 0 && ti < tipos.length) ? tipos[ti] : "STANDARD";

        Map<String, Object> body = new HashMap<>();
        body.put("nome", nome);
        body.put("capacidade", capacidade);
        body.put("tipo", tipo);

        JsonNode res = HttpHelper.post(baseUrl + "/salas", body);
        System.out.println("  Sala cadastrada:");
        imprimirSala(res);
    }

    private void atualizar() {
        System.out.print("  ID da sala a atualizar: ");
        String id = sc.nextLine().trim();
        System.out.print("  Nome: ");
        String nome = sc.nextLine().trim();
        System.out.print("  Capacidade: ");
        int capacidade = Integer.parseInt(sc.nextLine().trim());
        System.out.println("  Tipo: 1. STANDARD  2. IMAX  3. DOLBY  4. VIP  5. DRIVE_IN");
        System.out.print("  Opcao: ");
        String[] tipos = {"STANDARD", "IMAX", "DOLBY", "VIP", "DRIVE_IN"};
        int ti = Integer.parseInt(sc.nextLine().trim()) - 1;
        String tipo = (ti >= 0 && ti < tipos.length) ? tipos[ti] : "STANDARD";

        Map<String, Object> body = new HashMap<>();
        body.put("nome", nome);
        body.put("capacidade", capacidade);
        body.put("tipo", tipo);

        JsonNode res = HttpHelper.put(baseUrl + "/salas/" + id, body);
        System.out.println("  Sala atualizada:");
        imprimirSala(res);
    }

    private void deletar() {
        System.out.print("  ID da sala a deletar: ");
        String id = sc.nextLine().trim();
        HttpHelper.delete(baseUrl + "/salas/" + id);
        System.out.println("  Sala removida.");
    }

    private void imprimirSala(JsonNode s) {
        if (s.has("erro")) { System.out.println("  Erro: " + s.get("erro").asText()); return; }
        System.out.printf("  [%d] %s | Capacidade: %d | Tipo: %s%n",
                s.path("id").asLong(),
                s.path("nome").asText(),
                s.path("capacidade").asInt(),
                s.path("tipo").asText());
    }
}
