package com.cinelo.app.cli;

import com.cinelo.app.client.EventosClient;
import com.cinelo.app.dto.FilmeResponseDTO;
import com.cinelo.app.dto.PagamentoReservaRequestDTO;
import com.cinelo.app.dto.ReservaRequestDTO;
import com.cinelo.app.dto.ReservaResponseDTO;
import com.cinelo.app.dto.SessaoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class CatalogoCliFlow {

    private final EventosClient eventosClient;
    private final AutenticacaoCliFlow autenticacaoCliFlow;
    private final CliState cliState;

    public void executar(Scanner scanner) {
        System.out.println("\n--- Catálogo de Filmes ---");
        List<FilmeResponseDTO> filmes = eventosClient.listarFilmesAtivos();

        if (filmes == null || filmes.isEmpty()) {
            System.out.println("Nenhum filme em cartaz no momento.");
            return;
        }

        for (FilmeResponseDTO f : filmes) {
            System.out.println(f.getId() + " - " + f.getNome() + " (" + f.getClassificacaoIndicativa() + ")");
        }

        System.out.print("\nEscolha o ID do filme (ou 0 para voltar): ");
        Long filmeId = Long.parseLong(scanner.nextLine());
        if (filmeId == 0) return;

        System.out.println("\n--- Horários Disponíveis ---");
        List<SessaoResponseDTO> sessoes = eventosClient.listarSessoesPorFilme(filmeId);

        if (sessoes == null || sessoes.isEmpty()) {
            System.out.println("Nenhuma sessão disponível para este filme.");
            return;
        }

        for (SessaoResponseDTO s : sessoes) {
            System.out.println(s.getId() + " - Data/Hora: " + s.getDataHoraInicio() + " | Preço: R$" + s.getPreco() + " | Assentos: " + s.getAssentosDisponiveis());
        }

        System.out.print("\nEscolha o ID da sessão (ou 0 para voltar): ");
        Long sessaoId = Long.parseLong(scanner.nextLine());
        if (sessaoId == 0) return;

        if (!autenticacaoCliFlow.garantirLogin(scanner)) {
            System.out.println("Login necessário para continuar. Voltando ao menu...");
            return;
        }

        System.out.print("\nQuantos assentos deseja comprar (Até 5)? ");
        int assentos = Integer.parseInt(scanner.nextLine());
        if (assentos < 1 || assentos > 5) {
            System.out.println("Quantidade inválida. O limite é de 1 a 5 assentos.");
            return;
        }

        System.out.println("\nConfirmar seleção de " + assentos + " assentos? (S/N)");
        if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
            System.out.println("Operação cancelada.");
            return;
        }

        System.out.println("Criando reserva...");
        ReservaRequestDTO reservaDto = new ReservaRequestDTO();
        reservaDto.setSessaoId(sessaoId);
        reservaDto.setUsuarioId(cliState.getUsuarioLogadoId());
        reservaDto.setQuantidadeIngressos(assentos);

        try {
            ReservaResponseDTO reservaCriada = eventosClient.criarReserva(cliState.getToken(), reservaDto);
            System.out.println("Reserva criada! Código: " + reservaCriada.getCodigoConfirmacao() + " | Total: R$" + reservaCriada.getValorTotal());

            System.out.println("\n--- Tela de Pagamento ---");
            System.out.println("Escolha o tipo:");
            System.out.println("1 - Inteira");
            System.out.println("2 - Meia");
            System.out.print("Opção: ");
            scanner.nextLine();

            System.out.println("\nConfirmar pagamento simulado? (S/N)");
            if (!scanner.nextLine().trim().equalsIgnoreCase("S")) {
                System.out.println("Pagamento cancelado. A reserva ficará pendente.");
                return;
            }

            System.out.println("Processando pagamento...");
            PagamentoReservaRequestDTO pagDto = new PagamentoReservaRequestDTO();
            PagamentoReservaRequestDTO.CartaoDTO cartao = new PagamentoReservaRequestDTO.CartaoDTO();
            cartao.setNumero("1234567812345678");
            cartao.setNomeTitular(cliState.getUsuarioLogado().getNome());
            cartao.setMesExpiracao("12");
            cartao.setAnoExpiracao("2030");
            cartao.setCvv("123");
            pagDto.setCartao(cartao);

            ReservaResponseDTO reservaPaga = eventosClient.pagarReserva(cliState.getToken(), reservaCriada.getId(), pagDto);
            if ("CONFIRMADA".equals(reservaPaga.getStatus())) {
                System.out.println("\nSUCESSO: Pagamento Aprovado!");
            } else {
                System.out.println("\nFALHA no Pagamento: status=" + reservaPaga.getStatus());
            }
            System.out.println("Notificação: E-mail enviado ao cliente " + cliState.getUsuarioLogado().getEmail());

        } catch (Exception e) {
            System.out.println("\nFALHA no Pagamento: " + e.getMessage());
        }
        System.out.println("\nPressione ENTER para retornar ao Menu Principal...");
        scanner.nextLine();
    }
}
