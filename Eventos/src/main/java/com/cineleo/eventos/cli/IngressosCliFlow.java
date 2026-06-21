package com.cineleo.eventos.cli;

import com.cineleo.eventos.dto.ReservaResponseDTO;
import com.cineleo.eventos.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class IngressosCliFlow {

    private final ReservaService reservaService;
    private final AutenticacaoCliFlow autenticacaoCliFlow;
    private final CliState cliState;

    public void executar(Scanner scanner) {
        System.out.println("\n--- Meus Ingressos ---");
        if (!autenticacaoCliFlow.garantirLogin(scanner)) {
            System.out.println("Login necessário para acessar seus ingressos.");
            return;
        }

        List<ReservaResponseDTO> minhasReservas = reservaService.listarPorEmail(cliState.getUsuarioLogado().getEmail());
        if (minhasReservas.isEmpty()) {
            System.out.println("Você ainda não possui ingressos.");
        } else {
            for (ReservaResponseDTO r : minhasReservas) {
                System.out.println("-------------------------");
                System.out.println("Sessão ID: " + r.getSessaoId());
                System.out.println("Código: " + r.getCodigoConfirmacao());
                System.out.println("Status: " + r.getStatus());
                System.out.println("Assentos: " + r.getQuantidadeIngressos());
                System.out.println("Valor Pago: R$" + r.getValorTotal());
            }
        }
        System.out.println("\nPressione ENTER para voltar...");
        scanner.nextLine();
    }
}