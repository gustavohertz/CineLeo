package com.cinelo.app.cli;

import com.cinelo.app.client.EventosClient;
import com.cinelo.app.client.RecomendacaoClient;
import com.cinelo.app.dto.FilmeResponseDTO;
import com.cinelo.app.dto.RecomendacaoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecomendacaoCliFlow {

    private final RecomendacaoClient recomendacaoClient;
    private final EventosClient eventosClient;

    public void executar(Scanner scanner) {
        System.out.println("\n--- Filmes Recomendados (melhores avaliados) ---");

        List<RecomendacaoDTO> recomendacoes = recomendacaoClient.topRecomendados(10);

        if (recomendacoes == null || recomendacoes.isEmpty()) {
            System.out.println("Ainda não há recomendações (nenhuma avaliação registrada).");
        } else {
            Map<String, String> nomes = mapaDeNomes();
            int posicao = 1;
            for (RecomendacaoDTO r : recomendacoes) {
                String nome = nomes.getOrDefault(r.getFilmeId(), "Filme " + r.getFilmeId());
                System.out.printf("%d. %s - nota média %.1f (%d avaliações)%n",
                        posicao++, nome, r.getMediaNota(), r.getTotalAvaliacoes());
            }
        }

        System.out.println("\nPressione ENTER para voltar ao menu...");
        scanner.nextLine();
    }

    // Resolve filmeId -> nome usando o catálogo (o serviço de recomendação
    // trabalha só com ids). Se falhar, o flow mostra o id.
    private Map<String, String> mapaDeNomes() {
        try {
            List<FilmeResponseDTO> filmes = eventosClient.listarFilmesAtivos();
            return filmes.stream().collect(Collectors.toMap(
                    f -> String.valueOf(f.getId()), FilmeResponseDTO::getNome, (a, b) -> a));
        } catch (Exception e) {
            return Map.of();
        }
    }
}
