package com.cineleo.eventos.cli;

import com.cineleo.eventos.client.AvaliacaoClient;
import com.cineleo.eventos.dto.FilmeResponseDTO;
import com.cineleo.eventos.service.FilmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class AvaliacaoCliFlow {

    private final FilmeService filmeService;
    private final AutenticacaoCliFlow autenticacaoCliFlow;
    private final AvaliacaoClient avaliacaoClient;
    private final CliState cliState;

    public void executar(Scanner scanner) {
        System.out.println("\n--- Avaliar um Filme ---");
        List<FilmeResponseDTO> filmes = filmeService.listarAtivos();

        if (filmes.isEmpty()) {
            System.out.println("Nenhum filme em cartaz no momento para avaliar.");
            return;
        }

        for (FilmeResponseDTO f : filmes) {
            System.out.println(f.getId() + " - " + f.getNome() + " (" + f.getClassificacaoIndicativa() + ")");
        }

        System.out.print("\nEscolha o ID do filme que deseja avaliar (ou 0 para voltar): ");
        String inputFilme = scanner.nextLine();
        if (inputFilme.equals("0")) return;
        
        Long filmeId;
        try {
            filmeId = Long.parseLong(inputFilme);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        if (!autenticacaoCliFlow.garantirLogin(scanner)) {
            System.out.println("Login necessário para avaliar. Voltando ao menu...");
            return;
        }

        System.out.print("Sua nota para o filme (1.0 a 5.0): ");
        String notaInput = scanner.nextLine().replace(",", ".");
        Double nota;
        try {
            nota = Double.parseDouble(notaInput);
        } catch (NumberFormatException e) {
            System.out.println("Nota inválida. Operação cancelada.");
            return;
        }

        System.out.print("Deixe um comentário curto (opcional): ");
        String comentario = scanner.nextLine();

        System.out.println("Enviando avaliação...");
        AvaliacaoClient.AvaliacaoRequest request = AvaliacaoClient.AvaliacaoRequest.builder()
                .filmeId(String.valueOf(filmeId))
                .nota(nota)
                .comentario(comentario)
                .build();

        try {
            AvaliacaoClient.AvaliacaoResponse response = avaliacaoClient.criarAvaliacao(request, cliState.getToken());
            System.out.println("\nSUCESSO: Avaliação registrada com sucesso! (ID: " + response.getId() + ")");
        } catch (Exception e) {
            System.out.println("\nFALHA ao registrar avaliação: " + e.getMessage());
        }

        System.out.println("\nPressione ENTER para retornar ao Menu Principal...");
        scanner.nextLine();
    }
}
