package com.cineleo.cli.menu;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuUsuarios {

    private final String baseUrl;
    private final Scanner sc;

    public MenuUsuarios(String baseUrl, Scanner sc) {
        this.baseUrl = baseUrl;
        this.sc = sc;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n===== USUARIOS =====");
            System.out.println("1. Listar todos");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Buscar por email");
            System.out.println("4. Cadastrar usuario");
            System.out.println("5. Atualizar usuario");
            System.out.println("6. Inativar usuario");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1" -> listar();
                case "2" -> buscarPorId();
                case "3" -> buscarPorEmail();
                case "4" -> cadastrar();
                case "5" -> atualizar();
                case "6" -> inativar();
                case "0" -> voltar = true;
                default -> System.out.println("  Opcao invalida.");
            }
        }
    }

    private void listar() {
        JsonNode res = HttpHelper.get(baseUrl + "/usuarios");
        System.out.println("\n--- Usuarios ---");
        if (res.isArray() && res.size() == 0) { System.out.println("  Nenhum usuario cadastrado."); return; }
        if (res.isArray()) { for (JsonNode u : res) imprimirUsuario(u); }
        else System.out.println("  " + res);
    }

    private void buscarPorId() {
        System.out.print("  ID do usuario: ");
        String id = sc.nextLine().trim();
        imprimirUsuario(HttpHelper.get(baseUrl + "/usuarios/" + id));
    }

    private void buscarPorEmail() {
        System.out.print("  Email: ");
        String email = sc.nextLine().trim();
        imprimirUsuario(HttpHelper.get(baseUrl + "/usuarios/email?email=" + email));
    }

    private void cadastrar() {
        System.out.println("\n  -- Novo Usuario --");
        System.out.print("  Nome: ");       String nome = sc.nextLine().trim();
        System.out.print("  Idade: ");      int idade = Integer.parseInt(sc.nextLine().trim());
        System.out.print("  Email: ");      String email = sc.nextLine().trim();
        System.out.print("  CPF (11 digitos): "); String cpf = sc.nextLine().trim();

        Map<String, Object> body = new HashMap<>();
        body.put("nome", nome);
        body.put("idade", idade);
        body.put("email", email);
        body.put("cpf", cpf);

        JsonNode res = HttpHelper.post(baseUrl + "/usuarios", body);
        System.out.println("  Usuario cadastrado:");
        imprimirUsuario(res);
    }

    private void atualizar() {
        System.out.print("  ID do usuario a atualizar: ");
        String id = sc.nextLine().trim();
        System.out.print("  Nome: ");       String nome = sc.nextLine().trim();
        System.out.print("  Idade: ");      int idade = Integer.parseInt(sc.nextLine().trim());
        System.out.print("  Email: ");      String email = sc.nextLine().trim();
        System.out.print("  CPF (11 digitos): "); String cpf = sc.nextLine().trim();

        Map<String, Object> body = new HashMap<>();
        body.put("nome", nome);
        body.put("idade", idade);
        body.put("email", email);
        body.put("cpf", cpf);

        JsonNode res = HttpHelper.put(baseUrl + "/usuarios/" + id, body);
        System.out.println("  Usuario atualizado:");
        imprimirUsuario(res);
    }

    private void inativar() {
        System.out.print("  ID do usuario a inativar: ");
        String id = sc.nextLine().trim();
        HttpHelper.delete(baseUrl + "/usuarios/" + id);
        System.out.println("  Usuario inativado.");
    }

    private void imprimirUsuario(JsonNode u) {
        if (u.has("erro")) { System.out.println("  Erro: " + u.get("erro").asText()); return; }
        System.out.printf("  [%d] %s | Idade: %d | Email: %s | CPF: %s | %s%n",
                u.path("id").asLong(),
                u.path("nome").asText(),
                u.path("idade").asInt(),
                u.path("email").asText(),
                u.path("cpf").asText(),
                u.path("status").asText());
    }
}
