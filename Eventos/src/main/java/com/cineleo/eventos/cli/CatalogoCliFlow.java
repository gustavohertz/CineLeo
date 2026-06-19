package com.cineleo.eventos.cli;

import com.cineleo.eventos.dto.FilmeResponseDTO;
import com.cineleo.eventos.dto.PagamentoReservaRequestDTO;
import com.cineleo.eventos.dto.ReservaRequestDTO;
import com.cineleo.eventos.dto.ReservaResponseDTO;
import com.cineleo.eventos.dto.SessaoResponseDTO;
import com.cineleo.eventos.service.FilmeService;
import com.cineleo.eventos.service.ReservaService;
import com.cineleo.eventos.service.SessaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class CatalogoCliFlow {

    private final FilmeService filmeService;
    private final SessaoService sessaoService;
    private final ReservaService reservaService;
    private final AutenticacaoCliFlow autenticacaoCliFlow;
    private final CliState cliState;

    public void executar(Scanner scanner) {
        System.out.println("\n--- Catálogo de Filmes ---");
        List<FilmeResponseDTO> filmes = filmeService.listarAtivos();
        
        if (filmes.isEmpty()) {
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
        List<SessaoResponseDTO> sessoes = sessaoService.listarPorFilme(filmeId);
        
        if (sessoes.isEmpty()) {
            System.out.println("Nenhuma sessão disponível para este filme.");
            return;
        }

        for (SessaoResponseDTO s : sessoes) {
            System.out.println(s.getId() + " - Data/Hora: " + s.getDataHoraInicio() + " | Preço: R$" + s.getPreco() + " | Assentos: " + s.getAssentosDisponiveis());
        }

        System.out.print("\nEscolha o ID da sessão (ou 0 para voltar): ");
        Long sessaoId = Long.parseLong(scanner.nextLine());
        if (sessaoId == 0) return;

        autenticacaoCliFlow.garantirLogin(scanner);

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
        reservaDto.setUsuarioId(cliState.getUsuarioLogadoId());
        reservaDto.setSessaoId(sessaoId);
        reservaDto.setQuantidadeIngressos(assentos);
        
        ReservaResponseDTO reservaCriada = reservaService.criar(reservaDto);
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

        try {
            reservaService.pagar(reservaCriada.getId(), pagDto);
            System.out.println("\nSUCESSO: Pagamento Aprovado!");
            System.out.println("Notificação: E-mail enviado ao cliente " + cliState.getUsuarioLogado().getEmail());
        } catch (Exception e) {
            System.out.println("\nFALHA no Pagamento: " + e.getMessage());
            System.out.println("Notificação: E-mail de falha enviado para " + cliState.getUsuarioLogado().getEmail());
        }
        System.out.println("\nPressione ENTER para retornar ao Menu Principal...");
        scanner.nextLine();
    }
}
