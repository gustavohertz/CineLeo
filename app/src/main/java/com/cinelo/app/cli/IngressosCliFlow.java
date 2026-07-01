package com.cinelo.app.cli;

import com.cinelo.app.client.EventosClient;
import com.cinelo.app.dto.ReservaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class IngressosCliFlow {

    private final EventosClient eventosClient;
    private final AutenticacaoCliFlow autenticacaoCliFlow;
    private final CliState cliState;

    public void executar(Scanner scanner) {
        System.out.println("\n--- Meus Ingressos ---");
        if (!autenticacaoCliFlow.garantirLogin(scanner)) {
            System.out.println("Login necessário para acessar seus ingressos.");
            return;
        }

        List<ReservaResponseDTO> minhasReservas = eventosClient.listarReservasPorEmail(cliState.getToken(), cliState.getUsuarioLogado().getEmail());
        if (minhasReservas == null || minhasReservas.isEmpty()) {
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