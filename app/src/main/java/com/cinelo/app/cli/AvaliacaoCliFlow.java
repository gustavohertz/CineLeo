package com.cinelo.app.cli;

import com.cinelo.app.client.AvaliacaoClient;
import com.cinelo.app.client.EventosClient;
import com.cinelo.app.dto.AvaliacaoRequestDTO;
import com.cinelo.app.dto.AvaliacaoResponseDTO;
import com.cinelo.app.dto.FilmeResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class AvaliacaoCliFlow {

    private final AvaliacaoClient avaliacaoClient;
    private final EventosClient eventosClient;
    private final AutenticacaoCliFlow autenticacaoCliFlow;
    private final CliState cliState;

    public void executar(Scanner scanner) {
        System.out.println("\n--- Avaliar um Filme ---");

        List<FilmeResponseDTO> filmes = eventosClient.listarFilmesAtivos();
        if (filmes == null || filmes.isEmpty()) {
            System.out.println("Nenhum filme disponível para avaliar.");
            return;
        }
        for (FilmeResponseDTO f : filmes) {
            System.out.println(f.getId() + " - " + f.getNome());
        }

        System.out.print("\nEscolha o ID do filme (ou 0 para voltar): ");
        String filmeId = scanner.nextLine().trim();
        if ("0".equals(filmeId)) return;

        if (!autenticacaoCliFlow.garantirLogin(scanner)) {
            System.out.println("Login necessário para avaliar. Voltando ao menu...");
            return;
        }

        System.out.print("Nota (1 a 5): ");
        double nota;
        try {
            nota = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Nota inválida.");
            return;
        }
        if (nota < 1 || nota > 5) {
            System.out.println("A nota deve estar entre 1 e 5.");
            return;
        }

        System.out.print("Comentário: ");
        String comentario = scanner.nextLine();

        AvaliacaoRequestDTO request = new AvaliacaoRequestDTO();
        request.setFilmeId(filmeId);
        request.setNota(nota);
        request.setComentario(comentario);

        try {
            AvaliacaoResponseDTO resposta = avaliacaoClient.avaliar(cliState.getToken(), request);
            System.out.println("\nAvaliação registrada! Filme " + resposta.getFilmeId() + " - nota " + resposta.getNota());
        } catch (Exception e) {
            System.out.println("\nFalha ao avaliar: " + e.getMessage());
        }

        System.out.println("\nPressione ENTER para voltar ao menu...");
        scanner.nextLine();
    }
}
