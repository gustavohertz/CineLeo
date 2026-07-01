package com.cineleo.eventos.cli;

import com.cineleo.eventos.client.RecomendacaoClient;
import com.cineleo.eventos.service.FilmeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class RecomendacaoCliFlow {

    private final RecomendacaoClient recomendacaoClient;
    private final FilmeService filmeService;

    public void executar(Scanner scanner) {
        System.out.println("\n--- Filmes Recomendados (melhores avaliados) ---");

        List<RecomendacaoClient.RecomendacaoDTO> recomendacoes = recomendacaoClient.topRecomendados(10);

        if (recomendacoes.isEmpty()) {
            System.out.println("Ainda não há recomendações disponíveis (nenhuma avaliação registrada).");
        } else {
            int posicao = 1;
            for (RecomendacaoClient.RecomendacaoDTO r : recomendacoes) {
                System.out.printf("%d. %s - nota média %.1f (%d avaliações)%n",
                        posicao++, resolverNome(r.getFilmeId()), r.getMediaNota(), r.getTotalAvaliacoes());
            }
        }

        System.out.println("\nPressione ENTER para voltar ao menu...");
        scanner.nextLine();
    }

    // Best-effort: traduz o filmeId para o nome do filme (o serviço de
    // recomendação trabalha só com ids). Se não encontrar, mostra o id.
    private String resolverNome(String filmeId) {
        try {
            return filmeService.buscarPorId(Long.parseLong(filmeId)).getNome();
        } catch (Exception e) {
            return "Filme " + filmeId;
        }
    }
}
