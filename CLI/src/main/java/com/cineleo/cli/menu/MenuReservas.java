package com.cineleo.cli.menu;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuReservas {

    private final String baseUrl;
    private final Scanner sc;

    public MenuReservas(String baseUrl, Scanner sc) {
        this.baseUrl = baseUrl;
        this.sc = sc;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            System.out.println("\n===== RESERVAS =====");
            System.out.println("1. Buscar por ID");
            System.out.println("2. Buscar por codigo de confirmacao");
            System.out.println("3. Listar reservas de uma sessao");
            System.out.println("4. Listar minhas reservas (por email)");
            System.out.println("5. Fazer reserva");
            System.out.println("6. Pagar reserva");
            System.out.println("7. Cancelar reserva");
            System.out.println("0. Voltar");
            System.out.print("Opcao: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1" -> buscarPorId();
                case "2" -> buscarPorCodigo();
                case "3" -> listarPorSessao();
                case "4" -> listarPorEmail();
                case "5" -> criar();
                case "6" -> pagar();
                case "7" -> cancelar();
                case "0" -> voltar = true;
                default -> System.out.println("  Opcao invalida.");
            }
        }
    }

    private void buscarPorId() {
        System.out.print("  ID da reserva: ");
        String id = sc.nextLine().trim();
        imprimirReserva(HttpHelper.get(baseUrl + "/reservas/" + id));
    }

    private void buscarPorCodigo() {
        System.out.print("  Codigo de confirmacao: ");
        String codigo = sc.nextLine().trim();
        imprimirReserva(HttpHelper.get(baseUrl + "/reservas/codigo/" + codigo));
    }

    private void listarPorSessao() {
        System.out.print("  ID da sessao: ");
        String id = sc.nextLine().trim();
        JsonNode res = HttpHelper.get(baseUrl + "/reservas/sessao/" + id);
        if (res.isArray() && res.size() == 0) { System.out.println("  Nenhuma reserva para esta sessao."); return; }
        if (res.isArray()) { for (JsonNode r : res) imprimirReserva(r); }
        else System.out.println("  " + res);
    }

    private void listarPorEmail() {
        System.out.print("  Email: ");
        String email = sc.nextLine().trim();
        JsonNode res = HttpHelper.get(baseUrl + "/reservas/cliente?email=" + email);
        if (res.isArray() && res.size() == 0) { System.out.println("  Nenhuma reserva encontrada."); return; }
        if (res.isArray()) { for (JsonNode r : res) imprimirReserva(r); }
        else System.out.println("  " + res);
    }

    private void criar() {
        System.out.println("\n  -- Nova Reserva --");
        System.out.print("  ID da sessao: ");
        long sessaoId = Long.parseLong(sc.nextLine().trim());
        System.out.print("  ID do usuario: ");
        long usuarioId = Long.parseLong(sc.nextLine().trim());
        System.out.print("  Quantidade de ingressos (1-10): ");
        int qtd = Integer.parseInt(sc.nextLine().trim());

        Map<String, Object> body = new HashMap<>();
        body.put("sessaoId", sessaoId);
        body.put("usuarioId", usuarioId);
        body.put("quantidadeIngressos", qtd);

        JsonNode res = HttpHelper.post(baseUrl + "/reservas", body);
        System.out.println("  Reserva criada:");
        imprimirReserva(res);
        if (!res.has("erro")) {
            System.out.println("  Codigo de confirmacao: " + res.path("codigoConfirmacao").asText());
            System.out.println("  Use a opcao '6. Pagar reserva' para finalizar com ID: " + res.path("id").asLong());
        }
    }

    private void pagar() {
        System.out.println("\n  -- Pagamento da Reserva --");
        System.out.print("  ID da reserva: ");
        long reservaId = Long.parseLong(sc.nextLine().trim());

        System.out.println("  -- Dados do Cartao --");
        System.out.print("  Numero do cartao: ");    String numero = sc.nextLine().trim();
        System.out.print("  Nome do titular: ");     String titular = sc.nextLine().trim();
        System.out.print("  Mes de expiracao (MM): "); String mes = sc.nextLine().trim();
        System.out.print("  Ano de expiracao (YYYY): "); String ano = sc.nextLine().trim();
        System.out.print("  CVV: ");                 String cvv = sc.nextLine().trim();

        Map<String, Object> cartao = new HashMap<>();
        cartao.put("numero", numero);
        cartao.put("nomeTitular", titular);
        cartao.put("mesExpiracao", mes);
        cartao.put("anoExpiracao", ano);
        cartao.put("cvv", cvv);

        Map<String, Object> body = new HashMap<>();
        body.put("cartao", cartao);

        System.out.println("  Processando pagamento...");
        JsonNode res = HttpHelper.post(baseUrl + "/reservas/" + reservaId + "/pagar", body);
        imprimirReserva(res);

        if (!res.has("erro")) {
            String status = res.path("status").asText();
            if ("CONFIRMADA".equals(status)) {
                System.out.println("\n  Pagamento aprovado! Sua reserva esta CONFIRMADA.");
                System.out.println("  Codigo: " + res.path("codigoConfirmacao").asText());
            } else if ("PAGAMENTO_RECUSADO".equals(status)) {
                System.out.println("\n  Pagamento recusado. Tente outro cartao.");
            }
        }
    }

    private void cancelar() {
        System.out.print("  ID da reserva a cancelar: ");
        String id = sc.nextLine().trim();
        JsonNode res = HttpHelper.patch(baseUrl + "/reservas/" + id + "/cancelar");
        System.out.println("  Reserva cancelada:");
        imprimirReserva(res);
    }

    private void imprimirReserva(JsonNode r) {
        if (r.has("erro")) { System.out.println("  Erro: " + r.get("erro").asText()); return; }
        System.out.printf("  [%d] %s | %s | %dx R$ %.2f = R$ %.2f | Status: %s%n",
                r.path("id").asLong(),
                r.path("filmeNome").asText(),
                r.path("dataHoraSessao").asText().replace("T", " "),
                r.path("quantidadeIngressos").asInt(),
                r.path("valorTotal").asDouble() / Math.max(1, r.path("quantidadeIngressos").asInt()),
                r.path("valorTotal").asDouble(),
                r.path("status").asText());
        System.out.printf("  Cliente: %s | Sala: %s%n",
                r.path("nomeCliente").asText(),
                r.path("salaNome").asText());
    }
}
