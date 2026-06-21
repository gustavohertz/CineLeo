package com.cineleo.cli.menu;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuFilmes {

    private final String baseUrl;
    private final Scanner sc;

    public MenuFilmes(String baseUrl, Scanner sc) {
        this.baseUrl = baseUrl;
        this.sc = sc;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n===== FILMES =====");
            System.out.println("1. Listar todos");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Cadastrar filme");
            System.out.println("4. Atualizar filme");
            System.out.println("5. Inativar filme");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1" -> listar();
                case "2" -> buscarPorId();
                case "3" -> cadastrar();
                case "4" -> atualizar();
                case "5" -> inativar();
                case "0" -> voltar = true;
                default -> System.out.println("  Opcao invalida.");
            }
        }
    }

    private void listar() {
        JsonNode res = HttpHelper.get(baseUrl + "/filmes");
        System.out.println("\n--- Filmes ---");
        if (res.isArray() && res.size() == 0) {
            System.out.println("  Nenhum filme cadastrado.");
            return;
        }
        if (res.isArray()) {
            for (JsonNode f : res) imprimirFilme(f);
        } else {
            System.out.println("  " + res);
        }
    }

    private void buscarPorId() {
        System.out.print("  ID do filme: ");
        String id = sc.nextLine().trim();
        JsonNode res = HttpHelper.get(baseUrl + "/filmes/" + id);
        imprimirFilme(res);
    }

    private void cadastrar() {
        System.out.println("\n  -- Novo Filme --");
        System.out.print("  Nome: ");
        String nome = sc.nextLine().trim();

        System.out.println("  Classificacao indicativa:");
        System.out.println("  1. LIVRE  2. DOZE_ANOS  3. QUATORZE_ANOS  4. DEZESSEIS_ANOS  5. DEZOITO_ANOS");
        System.out.print("  Opcao: ");
        String[] classificacoes = {"LIVRE", "DOZE_ANOS", "QUATORZE_ANOS", "DEZESSEIS_ANOS", "DEZOITO_ANOS"};
        int ci = Integer.parseInt(sc.nextLine().trim()) - 1;
        String classif = (ci >= 0 && ci < classificacoes.length) ? classificacoes[ci] : "LIVRE";

        System.out.println("  -- Endereco --");
        System.out.print("  Logradouro: ");  String logradouro = sc.nextLine().trim();
        System.out.print("  Numero: ");      String numero = sc.nextLine().trim();
        System.out.print("  Complemento (Enter para pular): "); String complemento = sc.nextLine().trim();
        System.out.print("  Bairro: ");      String bairro = sc.nextLine().trim();
        System.out.print("  Cidade: ");      String cidade = sc.nextLine().trim();
        System.out.print("  UF (ex: SP): "); String uf = sc.nextLine().trim();
        System.out.print("  CEP (ex: 01310-100): "); String cep = sc.nextLine().trim();

        Map<String, Object> endereco = new HashMap<>();
        endereco.put("logradouro", logradouro);
        endereco.put("numero", numero);
        if (!complemento.isEmpty()) endereco.put("complemento", complemento);
        endereco.put("bairro", bairro);
        endereco.put("cidade", cidade);
        endereco.put("uf", uf);
        endereco.put("cep", cep);

        Map<String, Object> body = new HashMap<>();
        body.put("nome", nome);
        body.put("classificacaoIndicativa", classif);
        body.put("endereco", endereco);

        JsonNode res = HttpHelper.post(baseUrl + "/filmes", body);
        System.out.println("  Filme cadastrado:");
        imprimirFilme(res);
    }

    private void atualizar() {
        System.out.print("  ID do filme a atualizar: ");
        String id = sc.nextLine().trim();
        System.out.println("  (Preencha os novos dados)");

        System.out.print("  Nome: ");
        String nome = sc.nextLine().trim();

        System.out.println("  Classificacao indicativa:");
        System.out.println("  1. LIVRE  2. DOZE_ANOS  3. QUATORZE_ANOS  4. DEZESSEIS_ANOS  5. DEZOITO_ANOS");
        System.out.print("  Opcao: ");
        String[] classificacoes = {"LIVRE", "DOZE_ANOS", "QUATORZE_ANOS", "DEZESSEIS_ANOS", "DEZOITO_ANOS"};
        int ci = Integer.parseInt(sc.nextLine().trim()) - 1;
        String classif = (ci >= 0 && ci < classificacoes.length) ? classificacoes[ci] : "LIVRE";

        System.out.println("  -- Endereco --");
        System.out.print("  Logradouro: ");  String logradouro = sc.nextLine().trim();
        System.out.print("  Numero: ");      String numero = sc.nextLine().trim();
        System.out.print("  Complemento (Enter para pular): "); String complemento = sc.nextLine().trim();
        System.out.print("  Bairro: ");      String bairro = sc.nextLine().trim();
        System.out.print("  Cidade: ");      String cidade = sc.nextLine().trim();
        System.out.print("  UF: ");          String uf = sc.nextLine().trim();
        System.out.print("  CEP: ");         String cep = sc.nextLine().trim();

        Map<String, Object> endereco = new HashMap<>();
        endereco.put("logradouro", logradouro);
        endereco.put("numero", numero);
        if (!complemento.isEmpty()) endereco.put("complemento", complemento);
        endereco.put("bairro", bairro);
        endereco.put("cidade", cidade);
        endereco.put("uf", uf);
        endereco.put("cep", cep);

        Map<String, Object> body = new HashMap<>();
        body.put("nome", nome);
        body.put("classificacaoIndicativa", classif);
        body.put("endereco", endereco);

        JsonNode res = HttpHelper.put(baseUrl + "/filmes/" + id, body);
        System.out.println("  Filme atualizado:");
        imprimirFilme(res);
    }

    private void inativar() {
        System.out.print("  ID do filme a inativar: ");
        String id = sc.nextLine().trim();
        HttpHelper.delete(baseUrl + "/filmes/" + id);
        System.out.println("  Filme inativado.");
    }

    private void imprimirFilme(JsonNode f) {
        if (f.has("erro")) { System.out.println("  Erro: " + f.get("erro").asText()); return; }
        System.out.printf("  [%d] %s | %s | %s%n",
                f.path("id").asLong(),
                f.path("nome").asText(),
                f.path("classificacaoIndicativa").asText(),
                f.path("status").asText());
        JsonNode end = f.path("endereco");
        if (!end.isMissingNode()) {
            System.out.printf("      %s, %s - %s/%s%n",
                    end.path("logradouro").asText(),
                    end.path("numero").asText(),
                    end.path("cidade").asText(),
                    end.path("uf").asText());
        }
    }
}
