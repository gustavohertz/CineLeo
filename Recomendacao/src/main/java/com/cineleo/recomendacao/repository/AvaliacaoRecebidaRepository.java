package com.cineleo.recomendacao.repository;

import com.cineleo.recomendacao.entity.AvaliacaoRecebida;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvaliacaoRecebidaRepository extends JpaRepository<AvaliacaoRecebida, String> {

    // Projeção do ranking: filme + média das notas + total de avaliações.
    interface FilmeRanking {
        String getFilmeId();
        Double getMedia();
        Long getTotal();
    }

    // Top filmes por média, exigindo um mínimo de avaliações (evita que um
    // filme com 1 nota 10 fique acima de um filme com 50 notas 9).
    @Query("""
            select a.filmeId as filmeId, avg(a.nota) as media, count(a) as total
            from AvaliacaoRecebida a
            group by a.filmeId
            having count(a) >= :minAvaliacoes
            order by avg(a.nota) desc
            """)
    List<FilmeRanking> ranking(@Param("minAvaliacoes") long minAvaliacoes, Pageable pageable);

    // Mesmo ranking, mas excluindo filmes que o usuário já avaliou.
    @Query("""
            select a.filmeId as filmeId, avg(a.nota) as media, count(a) as total
            from AvaliacaoRecebida a
            where a.filmeId not in (
                select b.filmeId from AvaliacaoRecebida b where b.usuarioId = :usuarioId
            )
            group by a.filmeId
            having count(a) >= :minAvaliacoes
            order by avg(a.nota) desc
            """)
    List<FilmeRanking> rankingParaUsuario(@Param("usuarioId") String usuarioId,
                                          @Param("minAvaliacoes") long minAvaliacoes,
                                          Pageable pageable);
}
